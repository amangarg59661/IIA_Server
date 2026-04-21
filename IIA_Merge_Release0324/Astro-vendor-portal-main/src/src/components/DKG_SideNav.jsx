import { Divider, Layout, Menu } from "antd";
import Sider from "antd/es/layout/Sider";
import React, { useContext } from "react";
import { Link, useLocation, useNavigate } from "react-router-dom";
import {
  CheckOutlined,
  FileDoneOutlined,
  LogoutOutlined,
  FileTextOutlined,
  UserOutlined,
  FileAddOutlined,
  EditOutlined,
  FileExclamationOutlined,
  MoneyCollectOutlined,
  SafetyCertificateOutlined,
  CodeSandboxOutlined,
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
import { ActiveTabContext } from "../context/dashboardActiveTabContext";

const items = [
  {
    key: "1",
    icon: <DashboardOutlined />,
    label: "Dashboard",
    activeTab: 1,
    path: "/dashboard",
  },
  {
    key: "2",
    icon: <UsergroupAddOutlined />,
    label: "Queue",
    activeTab: 2,
    path: "/queue",
  },
  {
    key: "6",
    icon: <FileTextOutlined />,
    label: "Reports",
    // activeTab: 2,
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
                    activeTab: 3,
                    path: "/procurement/indent/creation",
                },
                // {
                //     key: "3.1.2",
                //     icon: <FileDoneOutlined />,
                //     label: "Indent Approval",
                //     activeTab: 4,
                //     path: "/",
                // },
                // {
                //     key: "3.1.3",
                //     icon: <EditOutlined />,
                //     label: "Indent Modification",
                //     activeTab: 5,
                //     path: "/procurement/indent/modification",
                // },
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
                    activeTab: 6,
                    path: "/procurement/tender/request",
                },
                {
                    key: "5",
                    icon: <FileTextOutlined />,
                    label: "Tender Evaluation",
                    activeTab: 7,
                    path: "/procurement/tender/evaluation",
                },
            ]
        },
        {
            key: "3.3",
            icon: <MoneyCollectOutlined />,
            label: "Purchase Order (PO)",
            activeTab: 9,
            path: "/procurement/purchaseOrder",
          },
          {
            key: "3.4",
            icon: <MoneyCollectOutlined />,
            label: "Service Order",
            activeTab: 23,
            path: "/procurement/serviceOrder",
          },
          {
            key: "3.5",
            icon: <MoneyCollectOutlined />,
            label: "Contingency Purchase",
            activeTab: 24,
            path: "/procurement/contingencyPurchase",
        },
        // {
        //     key: "3.6",
        //     icon: <UserOutlined />,
        //     label: "Job Creation",
        //     activeTab: 26,
        //     path: "/procurement/jobCreation",
        //   },
          
        //   {
        //     key: "3.7",
        //     icon: <CheckOutlined />,
        //     label: "Work Creation",
        //     activeTab: 27,
        //     path: "/procurement/workCreation",
        //   },
        //   {
        //     key: "3.8",
        //     icon: <SafetyCertificateOutlined />,
        //     label: "Performance & Warranty Security",
        //     activeTab: 11,
        //     path: "/procurement/performanceWarranty",
        //   },
        //   {
        //     key: "3.9",
        //     icon: <CodeSandboxOutlined />,
        //     label: "Delivery Tracking",
        //     activeTab: 12,
        //     path: "/procurement/deliveryTracking",
        //   },
    ]
  },
  {
    key: "4",
    label: "Inventory",
    items:[
        {
            key: "4.1",
            icon: <GoldOutlined />,
            label: "GPRN",
            activeTab: 13,
            path: "/inventory/gprn",
          },
          {
            key: "4.2",
            icon: <CheckSquareOutlined />,
            label: "Goods Inspection",
            activeTab: 14,
            path: "/inventory/goodsInspection",
          },
          {
            key: "4.3",
            icon: <RollbackOutlined />,
            label: "Goods Return",
            activeTab: 15,
            path: "/inventory/goodsReturn",
          },
          {
            key: "4.4",
            icon: <ReconciliationOutlined />,
            label: "Goods Receipt and Inspection",
            activeTab: 16,
            path: "/inventory/goodsReceipt",
          },
          {
            key: "4.5",
            icon: <ApartmentOutlined />,
            label: "Asset Master",
            activeTab: 17,
            path: "/inventory/assetMaster",
          },
          {
            key: "4.6",
            icon: <MdOutlineAddBox />,
            label: "Goods Issue",
            activeTab: 18,
            path: "/inventory/goodsIssue",
          },
          {
            key: "4.7",
            icon: <BiTransferAlt />,
            label: "Goods Transfer",
            activeTab: 19,
            path: "/inventory/goodsTransfer",
          },
          {
            key: "4.8",
            icon: <TiFolderDelete />,
            label: "Material Disposal",
            activeTab: 20,
            path: "/inventory/materialDisposal",
          },
          {
            key: "4.9",
            icon: <CiPassport1 />,
            label: "Gate Pass",
            activeTab: 21,
            path: "/inventory/gatePass",
          },
          {
            key: "4.20",
            icon: <GoIssueReopened />,
            label: "Demand and Issue",
            activeTab: 22,
            path: "/inventory/demandIssue",
        },
    ]
  },
  {
    key: "5",
    icon: <MdOutlineSettings />,
    label: "Masters",
    activeTab: 25,
    path: "/masters",
  },
];

const SideNav = ({ collapsed, toggleCollapse }) => {
  const location = useLocation();
  const currentPath = location.pathname;
  const { setActiveTab, activeTab } = useContext(ActiveTabContext);
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

  const handleMenuItemClick = (activeTab = null) => {
    setActiveTab(activeTab);
    if (window.innerWidth <= 768) {
      toggleCollapse();
    }
  };

  const displaySideNavItems = (item) => {
    if (!item.items) {
      return (
        <Menu.Item
          key={item.key}
          icon={item.icon}
          onClick={() => handleMenuItemClick(item.activeTab)}
          className={`${activeTab === item.activeTab ? "ant-menu-item-selected" : ""}`}
        >
          <Link to={item.path}>{item.label}</Link>
        </Menu.Item>
      );
    }

    return (
      <Menu.SubMenu key={item.key} icon={item.icon} title={item.label}>
        {item.items.map((child) => displaySideNavItems(child))}
      </Menu.SubMenu>
    );
  };

  const menuItems = items.map(displaySideNavItems);

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
        >
          {menuItems}
        </Menu>
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