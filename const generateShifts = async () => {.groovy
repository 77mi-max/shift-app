const generateShifts = async () => {
  const staffList = await base44.entities.Staff.list();

  const startDate = new Date();
  const days = 30;

  // カウント管理（夜勤回数）
  const nightCount = {};
  staffList.forEach(s => nightCount[s.id] = 0);

  for (let i = 0; i < days; i++) {
    const date = new Date();
    date.setDate(startDate.getDate() + i);

    // 夜勤担当（上限考慮）
    const nightCandidates = staffList.filter(
      s => nightCount[s.id] < (s.max_night_shifts || 5)
    );

    const nightStaff = nightCandidates[
      Math.floor(Math.random() * nightCandidates.length)
    ];

    nightCount[nightStaff.id]++;

    // 日勤（夜勤以外から2人）
    const dayCandidates = staffList.filter(s => s.id !== nightStaff.id);

    const dayStaff1 = dayCandidates[Math.floor(Math.random() * dayCandidates.length)];
    const dayStaff2 = dayCandidates[Math.floor(Math.random() * dayCandidates.length)];

    // 保存
    await base44.entities.Shift.create({
      date: date.toISOString(),
      type: "夜勤",
      staff: nightStaff.id
    });

    await base44.entities.Shift.create({
      date: date.toISOString(),
      type: "日勤",
      staff: dayStaff1.id
    });

    await base44.entities.Shift.create({
      date: date.toISOString(),
      type: "日勤",
      staff: dayStaff2.id
    });
  }

  alert("シフト作成完了！");
};
<Button onClick={generateShifts}>
  シフト自動作成（30日）
</Button>
const [shifts, setShifts] = useState([]);

const loadShifts = async () => {
  const data = await base44.entities.Shift.list();
  setShifts(data);
};

useEffect(() => {
  loadShifts();
}, []);
<div className="bg-card p-4 rounded-xl border">
  <h2 className="font-bold mb-4">シフト表</h2>

  {shifts.map((s) => (
    <div key={s.id} className="text-sm border-b py-2">
      {new Date(s.date).toLocaleDateString()} /
      {s.type} /
      {s.staff?.name || "未割当"}
    </div>
  ))}
</div>∫