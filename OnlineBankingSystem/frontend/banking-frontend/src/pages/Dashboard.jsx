import { useState, useEffect } from "react";
import { useAuth } from "../AuthContext";
import API from "../api";
import { ArrowDownCircle, ArrowUpCircle, LogOut, Send, Wallet, PlusCircle } from "lucide-react";

export default function Dashboard() {
  const { logout, user, token } = useAuth();
  const [balance, setBalance] = useState(null);
  const [transfer, setTransfer] = useState({ to: "", amount: "" });
  const [depositAmount, setDepositAmount] = useState("");
  const [transactions, setTransactions] = useState([]);
  const [filter, setFilter] = useState({ type: "ALL", start: "", end: "" });
  const [activeMenu, setActiveMenu] = useState("balance");
  const [loadingTransactions, setLoadingTransactions] = useState(false);

  useEffect(() => {
    if (user?.accountNumber && token) {
      getBalance();
      loadTransactions();
    }
  }, [user, token]);

  const getBalance = async () => {
    try {
      const res = await API.get(`/account/${user.accountNumber}/balance`);
      setBalance(res.data);
    } catch (err) {
      console.error(err);
      alert("Failed to fetch balance");
    }
  };

  const makeTransfer = async () => {
    const toTrim = transfer.to?.trim();
    const amountNum = parseFloat(transfer.amount);
    if (!toTrim || !transfer.amount || Number.isNaN(amountNum) || amountNum <= 0) {
      alert("Please enter a valid recipient account and positive amount");
      return;
    }
    try {
      await API.post(`/account/transfer`, {
        fromAccount: user.accountNumber.trim(),
        toAccount: toTrim,
        amount: amountNum,
      });
      alert("✅ Transfer successful");
      setTransfer({ to: "", amount: "" });
      await getBalance();
      await loadTransactions();
    } catch (err) {
      console.error(err);
      alert("❌ Transfer failed: " + (err.response?.data || err.message));
    }
  };

  const makeDeposit = async () => {
    const amountNum = parseFloat(depositAmount);
    if (!depositAmount || Number.isNaN(amountNum) || amountNum <= 0) {
      alert("Please enter a positive deposit amount");
      return;
    }
    try {
      
      await API.post(
        `/account/${user.accountNumber}/deposit`,
        null,
        { params: { amount: amountNum } }
      );
      alert("✅ Deposit successful");
      setDepositAmount("");
      await getBalance();
      await loadTransactions();
    } catch (err) {
      console.error(err);
      alert("❌ Deposit failed: " + (err.response?.data || err.message));
    }
  };

  const loadTransactions = async () => {
    if (!user?.accountNumber || !token) return;

    setLoadingTransactions(true);
    try {
      const params = {
        accountNumber: user.accountNumber,
      };
      if (filter.type && filter.type !== "ALL") params.type = filter.type;
      if (filter.start) params.start = filter.start;
      if (filter.end) params.end = filter.end;

      const res = await API.get(`/transactions`, { params });
      setTransactions(res.data);
    } catch (err) {
      console.error(err);
      alert("Failed to load transactions");
    } finally {
      setLoadingTransactions(false);
    }
  };

  return (
    <div className="min-h-screen flex flex-col bg-gradient-to-br from-gray-50 to-blue-50">
      {/* Header */}
      <header className="bg-blue-700 text-white p-4 flex justify-between items-center shadow-md">
        <h1 className="font-bold text-xl">💳 Online Banking</h1>
        <div className="flex items-center gap-4">
          <span className="font-semibold">{user?.username}</span>
          <button
            onClick={logout}
            className="flex items-center gap-1 bg-red-500 px-3 py-1 rounded hover:bg-red-600 transition"
          >
            <LogOut size={16} /> Logout
          </button>
        </div>
      </header>

      <div className="flex flex-1">
        {/* Sidebar */}
        <aside className="w-64 bg-white shadow-lg p-4">
          <h2 className="font-bold text-lg mb-4">📂 Menu</h2>
          <ul className="space-y-2">
            {["balance", "transactions", "transfer", "deposit"].map((menu) => (
              <li key={menu}>
                <button
                  className={`w-full text-left px-3 py-2 rounded transition ${activeMenu === menu ? "bg-blue-600 text-white" : "hover:bg-blue-100"}`}
                  onClick={() => setActiveMenu(menu)}
                >
                  {menu === "balance" && "Account Balance"}
                  {menu === "transactions" && "Transactions"}
                  {menu === "transfer" && "Fund Transfer"}
                  {menu === "deposit" && "Deposit"}
                </button>
              </li>
            ))}
          </ul>
        </aside>

        {/* Main */}
        <main className="flex-1 p-6 space-y-6">
          {/* Account Balance */}
          {activeMenu === "balance" && (
            <div className="bg-gradient-to-r from-green-400 to-green-600 text-white p-6 rounded-xl shadow-lg flex items-center gap-6 justify-center md:justify-start">
              <Wallet size={50} />
              <div>
                <h2 className="font-bold text-xl">Current Balance</h2>
                {balance !== null ? (
                  <p className="text-3xl font-semibold mt-2">₹{balance}</p>
                ) : (
                  <p className="italic">Loading...</p>
                )}
              </div>
            </div>
          )}

          {/* Fund Transfer */}
          {activeMenu === "transfer" && (
            <div className="bg-white p-6 rounded-xl shadow-lg">
              <h2 className="font-bold text-lg mb-4">💸 Fund Transfer</h2>
              <div className="flex flex-col md:flex-row gap-3">
                <input
                  placeholder="To Account"
                  value={transfer.to}
                  onChange={(e) => setTransfer({ ...transfer, to: e.target.value })}
                  className="border p-2 rounded flex-1"
                />
                <input
                  type="number"
                  placeholder="Amount"
                  value={transfer.amount}
                  onChange={(e) => setTransfer({ ...transfer, amount: e.target.value })}
                  className="border p-2 rounded flex-1"
                />
                <button
                  onClick={makeTransfer}
                  className="flex items-center gap-1 bg-blue-600 text-white px-4 py-2 rounded hover:bg-blue-700 transition"
                >
                  <Send size={16} /> Send
                </button>
              </div>
            </div>
          )}

          {/* Deposit */}
          {activeMenu === "deposit" && (
            <div className="bg-white p-6 rounded-xl shadow-lg">
              <h2 className="font-bold text-lg mb-4">💰 Deposit Funds</h2>
              <div className="flex flex-col md:flex-row gap-3">
                <input
                  type="number"
                  placeholder="Amount"
                  value={depositAmount}
                  onChange={(e) => setDepositAmount(e.target.value)}
                  className="border p-2 rounded flex-1"
                />
                <button
                  onClick={makeDeposit}
                  className="flex items-center gap-1 bg-green-600 text-white px-4 py-2 rounded hover:bg-green-700 transition"
                >
                  <PlusCircle size={16} /> Deposit
                </button>
              </div>
            </div>
          )}

          {/* Transactions */}
          {activeMenu === "transactions" && (
            <div className="bg-white p-6 rounded-xl shadow-lg">
              <h2 className="font-bold text-lg mb-4">📊 Transaction History</h2>
              <div className="flex flex-col md:flex-row gap-3 mb-3 items-center">
                <select
                  value={filter.type}
                  onChange={(e) => setFilter({ ...filter, type: e.target.value })}
                  className="border p-2 rounded"
                >
                  <option value="ALL">All</option>
                  <option value="CREDIT">Credit</option>
                  <option value="DEBIT">Debit</option>
                </select>
                <input
                  type="date"
                  value={filter.start}
                  onChange={(e) => setFilter({ ...filter, start: e.target.value })}
                  className="border p-2 rounded"
                />
                <input
                  type="date"
                  value={filter.end}
                  onChange={(e) => setFilter({ ...filter, end: e.target.value })}
                  className="border p-2 rounded"
                />
                <button
                  onClick={loadTransactions}
                  disabled={loadingTransactions}
                  className="bg-green-500 text-white px-3 py-2 rounded hover:bg-green-600 transition disabled:opacity-50"
                >
                  Apply
                </button>
              </div>

              {loadingTransactions ? (
                <p className="italic">Loading transactions...</p>
              ) : (
                <div className="overflow-x-auto">
                  <table className="w-full border-collapse rounded overflow-hidden shadow-sm">
                    <thead>
                      <tr className="bg-blue-100 text-left">
                        <th className="px-3 py-2">ID</th>
                        <th className="px-3 py-2">Amount</th>
                        <th className="px-3 py-2">Type</th>
                        <th className="px-3 py-2">Date</th>
                      </tr>
                    </thead>
                    <tbody>
                      {transactions.map((t, i) => (
                        <tr key={t.id} className={`${i % 2 === 0 ? "bg-gray-50" : "bg-white"} hover:bg-blue-50`}>
                          <td className="px-3 py-2">{t.id}</td>
                          <td className="px-3 py-2 font-semibold flex items-center gap-1">
                            {t.type === "CREDIT" ? <ArrowDownCircle size={16} /> : <ArrowUpCircle size={16} />}
                            <span className={t.type === "CREDIT" ? "text-green-600" : "text-red-600"}>₹{t.amount}</span>
                          </td>
                          <td>{t.type}</td>
                          <td>{new Date(t.timestamp).toLocaleString()}</td>
                        </tr>
                      ))}
                    </tbody>
                  </table>
                </div>
              )}
            </div>
          )}
        </main>
      </div>

      {/* Footer */}
      <footer className="bg-gray-200 text-center p-4 mt-auto rounded-t">
        © 2025 Online Banking. All rights reserved.
      </footer>
    </div>
  );
}
