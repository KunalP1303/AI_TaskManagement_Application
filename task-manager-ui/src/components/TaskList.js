// import React from "react";
import TaskCard from "./TaskCard";

function TaskList({ tasks, onDelete}) {

    if (tasks.lenght === 0) {
        return <p>No Task Found.</p>;
    }

    return (
        <div>
            {tasks.map((task) => (
                <TaskCard key={task.id} task={task} onDelete={onDelete} />
            ))}
        </div>
    )
}

export default TaskList;