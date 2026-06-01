import React from "react";
// import { ReactComponent as Logo } from "../assets/images/logo.svg";
import MyLogo from "../assets/images/iia-logo.png";
import { MenuOutlined } from "@ant-design/icons";
import { useNavigate } from "react-router-dom";
import IconBtn from "./DKG_IconBtn"
import { useSelector, useDispatch } from "react-redux";
import { changeRole } from "../store/slice/authSlice";

const Header = ({toggleCollapse}) => {
  const navigate = useNavigate();
  const dispatch = useDispatch();

  // Get roles and selected role from Redux
  const roles = useSelector((state) => state.auth.roles);
  const selectedRole = useSelector((state) => state.auth.role);
  const userName = useSelector((state) => state.auth.userName);

   const handleRoleChange = (e) => {
    const newRole = e.target.value;
    dispatch(changeRole(newRole)); // Update role in Redux

    // ✅ Navigate to the appropriate default page for the new role
    if (newRole === "Admin") {
      navigate('/admin'); // Admin goes to Admin Dashboard
    } else {
      navigate('/'); // All other roles go to Main Dashboard
    }
  };

  return (
    <header className="bg-offWhite shadow-md py-4 px-4 flex justify-between items-center sticky top-0 w-full z-20">
      <div className="flex gap-4 items-center">
        <span>
          <IconBtn icon={MenuOutlined} className="shadow-none"  onClick={toggleCollapse}/>
        </span>
        <span onClick={() => navigate('/')}>
          <img src={MyLogo} height={10} width={50} />
        </span>
      </div>
    
       <div className="flex gap-4 items-center">
        <span>Hello {userName || "User"}!</span>
        {roles?.length > 0 && (
          <select
            value={selectedRole}
            onChange={handleRoleChange}
            className="border rounded px-2 py-1"
          >
            {roles.map((role) => (
              <option key={role.roleId} value={role.roleName}>
                {role.roleName}
              </option>
            ))}
          </select>
        )}
      </div>
    </header>
  );
};

export default Header;
