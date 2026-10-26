import { useEffect, useState } from "react";
import "./App.css"; // <-- Import the CSS file here

const API_URL = "http://localhost:8080/api/tickets";
const STATUSES = ["OPEN", "IN_PROGRESS", "RESOLVED", "CLOSED"];

function App() {
  const [tickets, setTickets] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  const [title, setTitle] = useState("");
  const [description, setDescription] = useState("");
  const [saving, setSaving] = useState(false);
  const [updatingIds, setUpdatingIds] = useState([]);

  async function updateStatus(id, status) {
    setError("");
    setUpdatingIds((current) => [...current, id]);
    try {
      const response = await fetch(`${API_URL}/${id}/status`, {
        method: "PATCH",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ status }),
      });
      if (!response.ok) {
        throw new Error(`Could not update ticket status (${response.status})`);
      }
      const updatedTicket = await response.json();
      setTickets((current) => current.map((ticket) =>
        ticket.id === id ? updatedTicket : ticket
      ));
    } catch (err) {
      setError(err.message);
    } finally {
      setUpdatingIds((current) => current.filter((ticketId) => ticketId !== id));
    }
  }

  useEffect(() => {
    const controller = new AbortController();

    async function loadTickets() {
      try {
        const response = await fetch(API_URL, { signal: controller.signal });
        if (!response.ok) {
          throw new Error(`Could not load tickets (${response.status})`);
        }
        const data = await response.json();
        setTickets(data);
      } catch (err) {
        if (err.name !== "AbortError") {
          setError(err.message);
        }
      } finally {
        setLoading(false);
      }
    }

    loadTickets();
    return () => controller.abort();
  }, []);

  async function handleSubmit(event) {
    event.preventDefault();
    setError("");
    setSaving(true);

    try {
      const response = await fetch(API_URL, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ title, description }),
      });

      if (!response.ok) {
        throw new Error(`Could not create ticket (${response.status})`);
      }

      const createdTicket = await response.json();
      setTickets((current) => [...current, createdTicket]);
      setTitle("");
      setDescription("");
    } catch (err) {
      setError(err.message);
    } finally {
      setSaving(false);
    }
  }

  if (loading) {
    return (
      <main>
        <p className="loading-state">Loading tickets...</p>
      </main>
    );
  }

  return (
    <main>
      <h1>Support Tickets</h1>

      {error && <div className="error-banner">Error: {error}</div>}

      <form onSubmit={handleSubmit}>
        <div className="form-group">
          <label htmlFor="title">Title</label>
          <input
            id="title"
            type="text"
            value={title}
            onChange={(e) => setTitle(e.target.value)}
            placeholder="Issue summary..."
            required
            disabled={saving}
          />
        </div>

        <div className="form-group">
          <label htmlFor="description">Description</label>
          <textarea
            id="description"
            value={description}
            onChange={(e) => setDescription(e.target.value)}
            placeholder="Describe the problem in detail..."
            required
            disabled={saving}
          />
        </div>

        <button type="submit" disabled={saving}>
          {saving ? "Creating..." : "Create Ticket"}
        </button>
      </form>

      <hr />

      {tickets.length === 0 ? (
        <p className="empty-state">No tickets yet. Create one above!</p>
      ) : (
        <div className="ticket-list">
          {tickets.map((ticket) => (
            <article key={ticket.id}>
              <div className="ticket-header">
                <h2>{ticket.title}</h2>
                <span className="status-badge">
                  {ticket.status || "OPEN"}
                </span>
              </div>
              <p>{ticket.description}</p>
              <label>
                Status{" "}
                <select
                  aria-label={`Status for ticket ${ticket.id}`}
                  value={ticket.status}
                  disabled={updatingIds.includes(ticket.id)}
                  onChange={(event) => updateStatus(ticket.id, event.target.value)}
                >
                  {STATUSES.map((status) => (
                    <option key={status} value={status}>
                      {status.replaceAll("_", " ")}
                    </option>
                  ))}
                </select>
              </label>
              {updatingIds.includes(ticket.id) && <span role="status"> Saving...</span>}
            </article>
          ))}
        </div>
      )}
    </main>
  );
}

export default App;
