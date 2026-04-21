import React from "react";
import { Layout } from "antd";
import { Outlet } from "react-router-dom";
import DKGHeader from "./DKG_Header";
 // Make sure the path is correct

const { Content } = Layout;

const CustomLayout = () => {
  return (
    <Layout style={{ minHeight: "100vh" }}>
      {/* Use custom header */}
      <DKGHeader />

      <Content style={{ margin: "24px", background: "#fff", padding: "20px" }}>
        <Outlet />
      </Content>
    </Layout>
  );
};

export default CustomLayout;

