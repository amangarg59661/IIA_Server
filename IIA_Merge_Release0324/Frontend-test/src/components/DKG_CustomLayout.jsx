import React, { useEffect, useState } from "react";
import Header from './DKG_Header'
import { Layout} from "antd";
import SideNav from "./DKG_SideNav";
import { Outlet } from "react-router-dom";
const { Content } = Layout;

const CustomLayout = () => {
  const [collapsed, setCollapsed] = useState(false);
  const toggleCollapse = () => {
    setCollapsed(!collapsed);
  };

  useEffect(() => {
    if (window.innerWidth <= 768) setCollapsed(true);
  }, []);
  return (
    <>
      <Header toggleCollapse={toggleCollapse} />
      <div className="flex max-h-[100vh] overflow-y-auto h-[calc(100vh-4.5rem)] relative">
          <SideNav collapsed={collapsed} toggleCollapse={toggleCollapse} />
        <Layout className="layout overflow-hidden flex-1">
          <Content className="md:px-8 md:py-4 flex flex-col gap-4 md:gap-8 overflow-auto md:overflow-hidden relative main-content">
            <div className="relative z-1 overflow-auto flex flex-col gap-4 md:gap-8">
              <Outlet />
            </div>
          </Content>
        </Layout>
      </div>
    </>
  );
};
export default CustomLayout;
