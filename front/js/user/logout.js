async function logout() {
    const request = {
        method: "POST",
        headers: {
            'Content-Type': 'application/json'
        }
    };
    await fetch("http://localhost:9091/user/logout", request);
    window.location.href = "/";
}