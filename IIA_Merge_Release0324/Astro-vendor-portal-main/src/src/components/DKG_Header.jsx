import React from "react";
import MyLogo from "../assets/iia-logo.png";
import { MenuOutlined, LogoutOutlined } from "@ant-design/icons";
import { useNavigate } from "react-router-dom";
import IconBtn from "./DKG_IconBtn";
import { useDispatch } from "react-redux";
import { logout } from "../store/slice/authSlice";

const DKGHeader = ({ toggleCollapse }) => {
  const navigate = useNavigate();
  const dispatch = useDispatch();

const handleLogout = () => {
    dispatch(logout());
    navigate("/");
  };

  return (
    <header className="bg-offWhite shadow-md py-4 px-4 flex justify-between items-center sticky top-0 w-full z-20">
      <div className="flex gap-4 items-center">
        <span>
          <IconBtn icon={MenuOutlined} className="shadow-none" onClick={toggleCollapse} />
        </span>
        <span onClick={() => navigate("/")}>
          <img src={MyLogo} height={10} width={50} alt="Logo" />
        </span>
      </div>

      <button
        onClick={handleLogout}
       className="text-gray-700 hover:text-black border border-gray-300 px-4 py-1 rounded-md transition duration-150"
      >
        <LogoutOutlined />
        Logout
      </button>
    </header>
  );
};

export default DKGHeader;
