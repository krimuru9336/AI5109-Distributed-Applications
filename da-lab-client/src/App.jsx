import './App.css';
import Ex1 from './modules/ex1';
import Ex2 from './modules/ex2';
import Home from './modules/home';
import { BrowserRouter, Routes, Route } from "react-router-dom";

function App() {

  return (
    <BrowserRouter>
      <Routes>
        <Route path="/" element={<Home />} />
        <Route path="/ex1" element={<Ex1 />} />
        <Route path="/ex2" element={<Ex2 />} />
      </Routes>
    </BrowserRouter>
  )
}

export default App
