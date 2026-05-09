function TaskCard({ task, onDelete }) {

    const getPriorityColour = (priority) => {
        switch (priority?.trim()?.toUpperCase()) {

            case "HIGH":
                return "#fa5252"; // red

            case "MEDIUM":
                return "#fab005"; // yellow

            case "LOW":
                return "#40c057"; // green

            default:
                return "#adb5bd";
        }
    };

    const getStatusColour = (status) => {
        if (status === "DONE") return "#40c057";
        if (status === "IN_PROGRESS") return "#339af0";
        return "#adb5bd"
    };


    return (

        <div
            style={{
                backgroundColor: "white",
                padding: "20px",
                borderRadius: "12px",
                marginBottom: "15px",
                boxShadow: "0 2px 8px rgba(0,0,0,0.08)"
            }}
        >
            <h4>{task.title}</h4>

            <div
                style={{
                    display: "flex",
                    alignItems: "center",
                    gap: "15px"
                }}
            >

                <div
                    style={{
                        display: "flex",
                        alignItems: "center",
                        gap: "10px",
                        marginBottom: "15px",
                        flexWrap: "wrap"
                    }}
                >

                    <span
                        style={{
                            backgroundColor: getPriorityColour(task.priority),
                            color: "white",
                            padding: "6px 14px",
                            borderRadius: "20px",
                            fontSize: "12px",
                            fontWeight: "bold"
                        }}
                    >
                        {task.priority}
                    </span>

                    <span
                        style={{
                            backgroundColor: getStatusColour(task.status),
                            color: "white",
                            padding: "6px 14px",
                            borderRadius: "20px",
                            fontSize: "12px",
                            fontWeight: "bold"
                        }}
                    >
                        {task.status}
                    </span>

                    <div style={{ marginTop: "15px" }}>
                        <p><b>AI Summary:</b> {task.summary || "Not Available"}</p>
                        <p><b>Effort:</b> {task.estimatedEffort || "Not Available"}</p>
                    </div>
                </div>

                <button
                    onClick={() => onDelete(task.id)}
                    style={{
                        marginLeft: "auto",
                        backgroundColor: "#fa5252",
                        color: "white",
                        border: "none",
                        padding: "10px 16px",
                        borderRadius: "8px",
                        cursor: "pointer"
                    }}
                >
                    Delete
                </button>
            </div>
        </div >
    )
}

export default TaskCard;