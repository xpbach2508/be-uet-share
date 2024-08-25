import React from "react"
import { Route, Routes } from "react-router-dom"
import Home from "./homePage/home"
import Admin from "./adminPage/admin"

class App extends React.Component {

    render() {
        return (
            <Routes>
                <Route path="/" element={<Home/>} />
                <Route path="/admin/*" element={<Admin/>} />
            </Routes>
        )
    }
}

export default App