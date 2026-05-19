import { Divider, Layout, Menu } from "antd";
import Sider from "antd/es/layout/Sider";
import React from "react";
import { Link, useLocation, useNavigate } from "react-router-dom";
import {
  LogoutOutlined,
  FileTextOutlined,
  FileAddOutlined,
  FileExclamationOutlined,
  MoneyCollectOutlined,
  GoldOutlined,
  CheckSquareOutlined,
  RollbackOutlined,
  ReconciliationOutlined,
  ApartmentOutlined,
  UsergroupAddOutlined,
  DashboardOutlined
} from "@ant-design/icons";
import { MdOutlineAddBox, MdOutlineSettings } from "react-icons/md";
import { BiTransferAlt } from "react-icons/bi";
import { TiFolderDelete } from "react-icons/ti";
import { CiPassport1 } from "react-icons/ci";
import { GoIssueReopened } from "react-icons/go";
import IconBtn from "./DKG_IconBtn";
import { useDispatch } from "react-redux";
import { logout } from "../store/slice/authSlice";
import { useSelector } from "react-redux";
import { sidebarMenus } from "./SideNavMenus";
/*
const items = [
  {
    key: "1",
    icon: <DashboardOutlined />,
    label: "Dashboard",
    path: "/",
  },
  {
    key: "2",
    icon: <UsergroupAddOutlined />,
    label: "Queue",
    path: "/queue",
  },
  {
    key: "6",
    icon: <FileTextOutlined />,
    label: "Reports",
    path: "/reports",
  },
  {
    key: "3",
    label: "Procurement",
    items:[
        {
            key: "3.1",
            label: "Indent",
            items:[
                {
                    key: "3.1.1",
                    icon: <FileAddOutlined />,
                    label: "Indent Creation",
                    path: "/procurement/indent/creation",
                },
            ]
        },
        {
            key: "3.2",
            label: "Tender",
            items:[
                {
                    key: "3.2.1",
                    icon: <FileExclamationOutlined />,
                    label: "Tender Request",
                    path: "/procurement/tender/request",
                },
                {
                    key: "3.2.2",
                    icon: <FileTextOutlined />,
                    label: "Tender Evaluation",
                    path: "/procurement/tender/evaluation",
                },
                {
                    key: "3.2.3",
                    icon: <FileTextOutlined />,
                    label: "Gem Tender Evaluation",
                    path: "/procurement/tender/gem",
                },
                {
                    key: "3.2.4",
                    icon: <FileTextOutlined />,
                    label: "Tender Evaluation (New)",
                    path: "/procurement/tender-evaluation",
                },
                {
                    key: "3.2.5",
                    icon: <FileTextOutlined />,
                    label: "GeM/Open/Global Evaluation",
                    path: "/procurement/gem-tender-evaluation",
                },
            ]
        },
        {
            key: "3.3",
            icon: <MoneyCollectOutlined />,
            label: "Purchase Order (PO)",
            path: "/procurement/purchaseOrder",
          },
          {
            key: "3.4",
            icon: <MoneyCollectOutlined />,
            label: "Service Order",
            path: "/procurement/serviceOrder",
          },
          {
            key: "3.5",
            icon: <MoneyCollectOutlined />,
            label: "Contingency Purchase",
            path: "/procurement/contingencyPurchase",
        },
    ]
  },
  {
    key: "4",
    label: "Inventory",
    items:[
      {
        key: "12",
        icon: <FileTextOutlined />,
        label: "Inventory Reports",
        path: "/invReports",
      },
        {
            key: "4.1",
            icon: <GoldOutlined />,
            label: "GPRN",
            path: "/inventory/gprn",
          },
          {
            key: "4.2",
            icon: <CheckSquareOutlined />,
            label: "Goods Inspection",
            path: "/inventory/goodsInspection",
          },
          {
            key: "4.3",
            icon: <RollbackOutlined />,
            label: "Goods Return",
            path: "/inventory/goodsReturn",
          },
          {
            key: "4.4",
            icon: <ReconciliationOutlined />,
            label: "Goods Receipt and Inspection",
            path: "/inventory/goodsReceipt",
          },
          {
            key: "4.5",
            icon: <ApartmentOutlined />,
            label: "Asset Master",
            path: "/inventory/assetMaster",
          },
          // {
          //   key: "4.6",
          //   icon: <MdOutlineAddBox />,
          //   label: "Goods Issue",
          //   path: "/inventory/goodsIssue",
          // },
          {
            key: "4.7",
            icon: <BiTransferAlt />,
            label: "Goods Transfer",
            path: "/inventory/goodsTransfer",
          },
          {
            key: "4.8",
            icon: <TiFolderDelete />,
            label: "Material Disposal",
            path: "/inventory/materialDisposal",
          },
           {
            key: "4.9",
            icon: <TiFolderDelete />,
            label: "Asset Auction",
            path: "/inventory/ForDisposalAssets",
          },
          {
            key: "4.21",
            icon: <CiPassport1 />,
            label: "Outward Gate Pass",
            path: "/inventory/outward",
          },
          {
            key: "4.22",
            icon: <CiPassport1 />,
            label: "Inward Gate Pass",
            path: "/inventory/inward",
          },
          {
            key: "4.20",
            icon: <GoIssueReopened />,
            label: "Demand and Issue",
            path: "/inventory/demandIssue",
        },
    ]
  },
  {
    key: "5",
    icon: <MdOutlineSettings />,
    label: "Masters",
    path: "/masters",
  },
];

const SideNav = ({ collapsed, toggleCollapse }) => {
  const location = useLocation();
  const currentPath = location.pathname;
  const dispatch = useDispatch();
  const navigate = useNavigate();

  const getSelectedKey = (item) => {
    if (item.path === currentPath) {
      return item.key;
    }
    if (item.items) {
      for (const child of item.items) {
        const key = getSelectedKey(child);
        if (key) {
          return key;
        }
      }
    }
    return null;
  };

  const selectedKey = items.reduce((acc, item) => {
    return acc || getSelectedKey(item);
  }, null);

  const handleMenuItemClick = () => {
    if (window.innerWidth <= 768) {
      toggleCollapse();
    }
  };

  // Convert menu items to new Ant Design items API format
  const convertToMenuItems = (item) => {
    if (!item.items) {
      return {
        key: item.key,
        icon: item.icon,
        label: <Link to={item.path} onClick={() => handleMenuItemClick()}>{item.label}</Link>,
      };
    }

    return {
      key: item.key,
      icon: item.icon,
      label: item.label,
      children: item.items.map((child) => convertToMenuItems(child)),
    };
  };

  const menuItems = items.map(convertToMenuItems);

  // Handler for logging out
  const handleLogout = () => {
    dispatch(logout());
    // Navigate to the login page after logging out
    navigate("/login");
  };

  return (
    <Layout
      style={{ flex: 0 }}
      className={`absolute md:static h-full w-fit bg-offWhite z-10 !flex !flex-col transition-all duration-150 ${
        collapsed ? "-translate-x-full md:-translate-x-0" : ""
      }`}
    >
      <Sider
        width={300}
        trigger={null}
        collapsible
        collapsed={collapsed}
        onCollapse={toggleCollapse}
        className="overflow-y-auto !bg-offWhite !w-[100vw] !flex-1 custom-sider-css"
      >
        <Menu
          mode="inline"
          defaultSelectedKeys={["1"]}
          selectedKeys={selectedKey ? [selectedKey] : []}
          className="!bg-offWhite"
          items={menuItems}
        />
      </Sider>
      <Divider className="m-0 w-4" />
      <IconBtn
        text="Logout"
        icon={LogoutOutlined}
        className="bg-offWhite overflow-hidden"
        onClick={handleLogout}
      />
    </Layout>
  );
};
*/

const SideNav = ({ collapsed, toggleCollapse }) => {
  const location = useLocation();
  const currentPath = location.pathname;
  const dispatch = useDispatch();
  const navigate = useNavigate();
  const { role } = useSelector((state) => state.auth); // role name 
  const items = sidebarMenus[role] || sidebarMenus.default;

  




  const getSelectedKey = (item) => {
    if (item.path === currentPath) {
      return item.key;
    }
    if (item.items) {
      for (const child of item.items) {
        const key = getSelectedKey(child);
        if (key) {
          return key;
        }
      }
    }
    return null;
  };

  const selectedKey = items.reduce((acc, item) => {
    return acc || getSelectedKey(item);
  }, null);

  const handleMenuItemClick = () => {
    if (window.innerWidth <= 768) {
      toggleCollapse();
    }
  };

  // Convert menu items to new Ant Design items API format
  const convertToMenuItems = (item) => {
    if (!item.items) {
      return {
        key: item.key,
        icon: item.icon,
        label: <Link to={item.path} onClick={() => handleMenuItemClick()}>{item.label}</Link>,
      };
    }

    return {
      key: item.key,
      icon: item.icon,
      label: item.label,
      children: item.items.map((child) => convertToMenuItems(child)),
    };
  };

  const menuItems = items.map(convertToMenuItems);

  // Handler for logging out
  const handleLogout = () => {
    dispatch(logout());
    // Navigate to the login page after logging out
    navigate("/login");
  };

  return (
    <Layout
      style={{ flex: 0 }}
      className={`absolute md:static h-full w-fit bg-offWhite z-10 !flex !flex-col transition-all duration-150 ${
        collapsed ? "-translate-x-full md:-translate-x-0" : ""
      }`}
    >
      <Sider
        width={300}
        trigger={null}
        collapsible
        collapsed={collapsed}
        onCollapse={toggleCollapse}
        className="overflow-y-auto !bg-offWhite !w-[100vw] !flex-1 custom-sider-css"
      >
        <Menu
          mode="inline"
          defaultSelectedKeys={["1"]}
          selectedKeys={selectedKey ? [selectedKey] : []}
          className="!bg-offWhite"
          items={menuItems}
        />
      </Sider>
      <Divider className="m-0 w-4" />
      <IconBtn
        text="Logout"
        icon={LogoutOutlined}
        className="bg-offWhite overflow-hidden"
        onClick={handleLogout}
      />
    </Layout>
  );
};


export default SideNav;