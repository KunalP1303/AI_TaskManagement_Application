import React, { useState } from "react";
import { login } from "../api/taskApi";

const Login = ({ onLogin }) => {
    const [username, setUsername] = useState("");
    const [password, setPassword] = useState("");
    const [error, setError] = useState("");

    const handleLogin = async (e) => {
        e.preventDefault();
        try {
            const res = await login({ username, password });

            // store token
            localStorage.setItem("token", res.data);

            onLogin();
        } catch (err) {
            setError("Invalid username or password");
        }
    };

    return (
        <form onSubmit={handleLogin}
            style={{
                minHeight: "100vh",
                display: "flex",
                justifyContent: "center",
                alignItems: "center",
                backgroundColor: "#f4f7fb",
                fontFamily: "Arial, sans-serif"
            }}
        >
            <div
                style={{
                    backgroundColor: "white",
                    padding: "40px",
                    borderRadius: "16px",
                    width: "350px",
                    boxShadow: "0 4px 16px rgba(0,0,0,0.1)"
                }}
            >
                <h1 style={{ marginBottom: "10px" }}>AI Task Manager</h1>

                <p
                    style={{
                        color: "gray",
                        marginBottom: "30px"
                    }}
                >
                    Sign in to continue
                </p>

                <input
                    type="text"
                    placeholder="Username"
                    value={username}
                    onChange={(e) => setUsername(e.target.value)}
                    style={{
                        width: "100%",
                        padding: "12px",
                        marginBottom: "15px",
                        borderRadius: "8px",
                        border: "1px solid #ccc",
                        boxSizing: "border-box"
                    }}
                />

                <input
                    type="password"
                    placeholder="Password"
                    value={password}
                    onChange={(e) => setPassword(e.target.value)}
                    style={{
                        width: "100%",
                        padding: "12px",
                        marginBottom: "20px",
                        borderRadius: "8px",
                        border: "1px solid #ccc",
                        boxSizing: "border-box"
                    }}
                />

                {error && (
                    <p
                        style={{
                            color: "#e03131",
                            marginBottom: "15px",
                            fontSize: "14px"
                        }}
                    >
                        Invalid username or password
                    </p>
                )}

                <button
                    type="submit"
                    style={{
                        width: "100%",
                        padding: "12px",
                        backgroundColor: "#1971c2",
                        color: "white",
                        border: "none",
                        borderRadius: "8px",
                        cursor: "pointer",
                        fontSize: "16px"
                    }}
                >
                    Login
                </button>
            </div>
        </form>
    );
};

export default Login;