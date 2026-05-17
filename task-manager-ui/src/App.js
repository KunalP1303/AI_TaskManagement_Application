import React, { useEffect, useState } from "react";
import Login from "./components/Login";
import TaskForm from "./components/TaskForm";
import TaskList from "./components/TaskList";
import { getTasks, createTask, deleteTask } from "./api/taskApi";



function App() {

  const [tasks, setTasks] = useState([]);
  const [, setLoading] = useState(false);
  const [error, setError] = useState(null);

  const [statusFilter, setStatusFilter] = useState("ALL");
  const [priorityFilter, setPriorityFilter] = useState("ALL");

  //New Auth state
  const [isLoggedIn, setIsLoggedIn] = useState(false);

  useEffect(() => {
    const token = localStorage.getItem("token");

    if (token) {
      setIsLoggedIn(true);
      fetchTasks();
    } else {
      setIsLoggedIn(false);
    }
  }, []);

  const fetchTasks = async () => {
    try {
      setLoading(true);
      const response = await getTasks();
      setTasks(response.data.data.content);

    } catch (err) {
      setError("Failed to fetch tasks");
      setTasks([]);
    } finally {
      setLoading(false);
    }
  };

  const handleCreateTask = async (task) => {
    await createTask(task);
    fetchTasks();
  };

  const handleDeleteTask = async (id) => {
    await deleteTask(id);
    fetchTasks();
  };

  if (!isLoggedIn) {
    return <Login onLogin={() => {
      setIsLoggedIn(true);
      fetchTasks();
    }} />
  }

  const handleLogout = () => {
    localStorage.removeItem("token");
    window.location.href = "/auth/login";
  };

  if (error) return <p>{error}</p>;
  const filteredTasks = Array.isArray(tasks)
    ? tasks.filter((task) => {
      return (
        (statusFilter === "ALL" || task.status === statusFilter) &&
        (priorityFilter === "ALL" || task.priority === priorityFilter)
      );
    })
    : [];



  return (
    <div
      style={{
        minHeight: "100vh",
        backgroundColor: "#f4f7fb",
        padding: "30px",
        fontFamily: "Arial, sans-serif"
      }}
    >
      <h2>Task Manager</h2>

      <div
        style={{
          display: "flex",
          justifyContent: "space-between",
          alignItems: "center",
          marginBottom: "30px"
        }}
      >
        <div>
          <h1 style={{ margin: 0 }}>AI Task Manager</h1>
          <p style={{ color: "gray", marginTop: "5px" }}>
            AI-powered productivity dashboard
          </p>
        </div>

        <button
          onClick={handleLogout}
          style={{
            padding: "10px 18px",
            backgroundColor: "#e03131",
            color: "white",
            border: "none",
            borderRadius: "8px",
            cursor: "pointer"
          }}
        >
          Logout
        </button>
      </div>

      <h3>Filters</h3>
      <div
        style={{
          backgroundColor: "white",
          padding: "20px",
          borderRadius: "12px",
          marginBottom: "25px",
          boxShadow: "0 2px 8px rgba(0,0,0,0.08)"
        }}
      >
        <select
          style={{
            padding: "10px",
            marginRight: "15px",
            borderRadius: "8px",
            border: "1px solid #ccc"
          }}
          value={statusFilter} onChange={(e) => setStatusFilter(e.target.value)}>
          <option value="ALL">All Status</option>
          <option value="TODO">TODO</option>
          <option value="IN_PROGRESS">IN_PROGRESS</option>
          <option value="DONE">DONE</option>
        </select>

        <select
          style={{
            padding: "10px",
            marginRight: "15px",
            borderRadius: "8px",
            border: "1px solid #ccc"
          }}
          value={priorityFilter} onChange={(e) => setPriorityFilter(e.target.value)}>
          <option value="ALL">All Priority</option>
          <option value="LOW">LOW</option>
          <option value="MEDIUM">MEDIUM</option>
          <option value="HIGH">HIGH</option>
        </select>
      </div>

      <h3>Create Task</h3>
      <div style={{ marginBottom: "20px" }}>
        <TaskForm onTaskCreated={handleCreateTask} />
      </div>

      <h3>Task List</h3>
      <div style={{ maxWidth: "700px", margin: "auto" }}>
        {filteredTasks.length === 0 ? (
          <div
            style={{
              backgroundColor: "white",
              padding: "30px",
              borderRadius: "12px",
              textAlign: "center",
              color: "gray",
              boxShadow: "0 2px 8px rgba(0,0,0,0.08)"
            }}
          >
            <h3>No tasks found</h3>
            <p>Create a task to get started</p>
          </div>
        ) : (
          <TaskList tasks={filteredTasks} onDelete={handleDeleteTask} />
        )}
      </div>
    </div>
  );
}

export default App;
