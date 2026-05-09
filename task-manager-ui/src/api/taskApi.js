import axios from 'axios';

const BASE_URL = "http://localhost:8080/";

// Create axio instance
const api = axios.create({
    baseURL: BASE_URL
});

// Attach token automatically
api.interceptors.request.use((config) =>{
    const token = localStorage.getItem("token");

    if(token){
        config.headers.Authorization = `Bearer ${token}`;
    }

    return config;
});

api.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response && error.response.status === 403) {
      
      // ❗ token invalid → logout
      localStorage.removeItem("token");

      // redirect to login
      window.location.href = "/auth/login";
    }

    return Promise.reject(error);
  }
);

// AUTH APIs
export const login = (data) => api.post("/auth/login", data);
export const register = (data) => api.post("/auth/register", data);

// TASK APIs
export const getTasks = () => api.get("/tasks");
export const createTask = (task) => api.post("/tasks", task);
export const deleteTask = (id) => api.delete(`/tasks/${id}`);

export default api;