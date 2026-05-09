import React, { useState } from "react";

function TaskForm({ onTaskCreated }) {
    const [title, setTitle] = useState("");
    const [description, setDescription] = useState("");
    const [status, setStatus] = useState("TODO");

    const handleSubmit = async () => {
        if (!title || !description) return;

        const newTask = {
            title,
            description,
            status,
        };

        await onTaskCreated(newTask);
        setTitle("");
        setDescription("");
        setStatus("TODO");

    };

    return (
        <div
            style={{
                backgroundColor: "white",
                padding: "20px",
                borderRadius: "12px",
                boxShadow: "0 2px 8px rgba(0,0,0,0.08)"
            }}
        >
            <input style={{
                padding: "10px",
                width: "250px",
                marginRight: "10px",
                borderRadius: "8px",
                border: "1px solid #ccc"
            }}
                value={title}
                onChange={(e) => setTitle(e.target.value)}
                placeholder="Enter Task"
            />

            <input style={{
                padding: "10px",
                width: "250px",
                marginRight: "10px",
                borderRadius: "8px",
                border: "1px solid #ccc"
            }}
                value={description}
                onChange={(e) => setDescription(e.target.value)}
                placeholder="Enter Description"
            />

            <select value={status}
                onChange={(e) => setStatus(e.target.value)}
                style={{ marginRight: "10px", padding: "8px" }}
            >
                <option value="TODO">TODO</option>
                <option value="IN_PROGRESS">IN_PROGRESS</option>
                <option value="DONE">DONE</option>
            </select>

            <button style={{
                padding: "8px 12px",
                backgroundColor: "#1971c2",
                color: "white",
                border: "none",
                borderRadius: "5px"
            }}
                onClick={handleSubmit}>ADD</button>
        </div>
    );
}

export default TaskForm;