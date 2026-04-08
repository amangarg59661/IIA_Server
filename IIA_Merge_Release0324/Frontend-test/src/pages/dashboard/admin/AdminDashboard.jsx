import React, { useEffect, useState } from 'react';
import { Card, Row, Col, Statistic, Spin } from 'antd';
import {
  UserOutlined,
  ProjectOutlined,
  DollarOutlined,
  TeamOutlined,
  FileTextOutlined,
  SettingOutlined,
  SafetyCertificateOutlined,
  ApartmentOutlined,
  EnvironmentOutlined,
  EyeOutlined
} from '@ant-design/icons';
import { useNavigate } from 'react-router-dom';
import axios from 'axios';

const AdminDashboard = () => {
  const navigate = useNavigate();
  const [loading, setLoading] = useState(true);
  const [stats, setStats] = useState({
    totalEmployees: 0,
    totalProjects: 0,
    totalBudget: 0,
    totalUsers: 0,
    activeWorkflows: 0,
    totalLOVs: 0
  });

  useEffect(() => {
    fetchDashboardStats();
  }, []);

  const fetchDashboardStats = async () => {
    try {
      setLoading(true);

      // Fetch all stats in parallel with better error logging
      const [employeesRes, projectsRes, budgetRes, usersRes, lovRes] = await Promise.all([
        axios.get('/api/employee-department-master').catch((err) => {
          console.error('Employees API Error:', err);
          return { data: { responseData: [] } };
        }),
        axios.get('/api/project-master').catch((err) => {
          console.error('Projects API Error:', err);
          return { data: { responseData: [] } };
        }),
        axios.get('/api/admin/budget/summary').catch((err) => {
          console.error('Budget Summary API Error:', err);
          return { data: { data: { totalAllocated: 0 } } };
        }),
        axios.get('/api/userMaster').catch((err) => {
          console.error('Users API Error:', err);
          return { data: { responseData: [] } };
        }),
        axios.get('/api/admin/lov/values/count').catch((err) => {
          console.error('LOV Count API Error:', err);
          return { data: { data: { totalCount: 0 } } };
        })
      ]);

      console.log('=== API Responses ===');
      console.log('Budget Response:', budgetRes.data);
      console.log('LOV Response:', lovRes.data);

      // Handle multiple response formats for budget
      let totalBudget = 0;
      if (budgetRes.data.status === 'success') {
        totalBudget = budgetRes.data.data?.totalAllocated || 0;
      } else if (budgetRes.data.data?.totalAllocated !== undefined) {
        totalBudget = budgetRes.data.data.totalAllocated;
      } else if (budgetRes.data.totalAllocated !== undefined) {
        totalBudget = budgetRes.data.totalAllocated;
      } else if (budgetRes.data.responseData?.totalAllocated !== undefined) {
        totalBudget = budgetRes.data.responseData.totalAllocated;
      }

      // Handle multiple response formats for LOV count
      let totalLOVs = 0;
      if (lovRes.data.status === 'success') {
        totalLOVs = lovRes.data.data?.totalCount || lovRes.data.data?.count || 0;
      } else if (lovRes.data.data?.totalCount !== undefined) {
        totalLOVs = lovRes.data.data.totalCount;
      } else if (lovRes.data.totalCount !== undefined) {
        totalLOVs = lovRes.data.totalCount;
      } else if (lovRes.data.count !== undefined) {
        totalLOVs = lovRes.data.count;
      } else if (lovRes.data.responseData?.totalCount !== undefined) {
        totalLOVs = lovRes.data.responseData.totalCount;
      } else if (lovRes.data.responseData?.count !== undefined) {
        totalLOVs = lovRes.data.responseData.count;
      }

      console.log('Parsed Budget:', totalBudget);
      console.log('Parsed LOVs:', totalLOVs);

      setStats({
        totalEmployees: employeesRes.data.responseData?.length || 0,
        totalProjects: projectsRes.data.responseData?.length || 0,
        totalBudget: totalBudget,
        totalUsers: usersRes.data.responseData?.length || 0,
        activeWorkflows: 7, // Hardcoded count of workflows
        totalLOVs: totalLOVs
      });
    } catch (error) {
      console.error('Error fetching dashboard stats:', error);
    } finally {
      setLoading(false);
    }
  };

  const adminModules = [
    {
      title: 'List of Values',
      icon: <FileTextOutlined style={{ fontSize: 32, color: '#1890ff' }} />,
      description: 'Manage form dropdown values and designators',
      path: '/admin/lov',
      color: '#e6f7ff'
    },
    {
      title: 'Approval Workflow',
      icon: <SettingOutlined style={{ fontSize: 32, color: '#52c41a' }} />,
      description: 'Configure workflow approvers and branches',
      path: '/admin/approvers',
      color: '#f6ffed'
    },
    {
      title: 'Approval Limits',
      icon: <SafetyCertificateOutlined style={{ fontSize: 32, color: '#f5222d' }} />,
      description: 'Configure approval limits by role, category, and department',
      path: '/admin/approval-limits',
      color: '#fff1f0'
    },
    {
      title: 'Department Approvers',
      icon: <ApartmentOutlined style={{ fontSize: 32, color: '#722ed1' }} />,
      description: 'Map departments to Dean/Head SEG for approvals',
      path: '/admin/department-approvers',
      color: '#f9f0ff'
    },
    {
      title: 'Field Station In-Charges',
      icon: <EnvironmentOutlined style={{ fontSize: 32, color: '#fa8c16' }} />,
      description: 'Configure Engineer/Professor In-Charge for field stations',
      path: '/admin/field-station-approvers',
      color: '#fff7e6'
    },
    {
      title: 'Full Workflow Config',
      icon: <EyeOutlined style={{ fontSize: 32, color: '#1890ff' }} />,
      description: 'View complete workflow configuration',
      path: '/admin/workflow-config',
      color: '#e6f7ff'
    },
    {
      title: 'Projects',
      icon: <ProjectOutlined style={{ fontSize: 32, color: '#13c2c2' }} />,
      description: 'Manage projects and budgets',
      path: '/admin/projects',
      color: '#e6fffb'
    },
    {
      title: 'Budget',
      icon: <DollarOutlined style={{ fontSize: 32, color: '#faad14' }} />,
      description: 'Track and allocate budgets',
      path: '/admin/budget',
      color: '#fffbe6'
    },
    {
      title: 'Employee Registration',
      icon: <UserOutlined style={{ fontSize: 32, color: '#eb2f96' }} />,
      description: 'Register and manage employees',
      path: '/admin/employee',
      color: '#fff0f6'
    },
    {
      title: 'User Creation',
      icon: <TeamOutlined style={{ fontSize: 32, color: '#2f54eb' }} />,
      description: 'Create user accounts and assign roles',
      path: '/admin/user',
      color: '#f0f5ff'
    }
  ];

  if (loading) {
    return (
      <div style={{ display: 'flex', justifyContent: 'center', alignItems: 'center', minHeight: '400px' }}>
        <Spin size="large" />
      </div>
    );
  }

  return (
    <div style={{ padding: '24px' }}>
      <h2 style={{ marginBottom: '24px', fontSize: '24px', fontWeight: 600 }}>Admin Dashboard</h2>

      {/* Statistics Cards */}
      <Row gutter={16} style={{ marginBottom: '32px' }}>
        <Col xs={24} sm={12} lg={8}>
          <Card>
            <Statistic
              title="Total Employees"
              value={stats.totalEmployees}
              prefix={<UserOutlined />}
              valueStyle={{ color: '#1890ff' }}
            />
          </Card>
        </Col>
        <Col xs={24} sm={12} lg={8}>
          <Card>
            <Statistic
              title="Total Projects"
              value={stats.totalProjects}
              prefix={<ProjectOutlined />}
              valueStyle={{ color: '#52c41a' }}
            />
          </Card>
        </Col>
        <Col xs={24} sm={12} lg={8}>
          <Card>
            <Statistic
              title="Total Budget Allocated"
              value={stats.totalBudget}
              // Modified by Aman
              prefix= "₹"
              // End
              valueStyle={{ color: '#722ed1' }}
              precision={2}
            />
          </Card>
        </Col>
        <Col xs={24} sm={12} lg={8}>
          <Card>
            <Statistic
              title="Total Users"
              value={stats.totalUsers}
              prefix={<TeamOutlined />}
              valueStyle={{ color: '#eb2f96' }}
            />
          </Card>
        </Col>
        <Col xs={24} sm={12} lg={8}>
          <Card>
            <Statistic
              title="Active Workflows"
              value={stats.activeWorkflows}
              prefix={<SettingOutlined />}
              valueStyle={{ color: '#fa8c16' }}
            />
          </Card>
        </Col>
        <Col xs={24} sm={12} lg={8}>
          <Card>
            <Statistic
              title="Total LOV Entries"
              value={stats.totalLOVs}
              prefix={<FileTextOutlined />}
              valueStyle={{ color: '#13c2c2' }}
            />
          </Card>
        </Col>
      </Row>

      {/* Admin Modules Grid */}
      <h3 style={{ marginBottom: '16px', fontSize: '18px', fontWeight: 600 }}>Admin Modules</h3>
      <Row gutter={[16, 16]}>
        {adminModules.map((module, index) => (
          <Col xs={24} sm={12} lg={8} key={index}>
            <Card
              hoverable
              onClick={() => navigate(module.path)}
              style={{
                height: '100%',
                backgroundColor: module.color,
                cursor: 'pointer',
                transition: 'all 0.3s ease'
              }}
              styles={{ body: { padding: '24px' } }}
            >
              <div style={{ textAlign: 'center' }}>
                <div style={{ marginBottom: '16px' }}>{module.icon}</div>
                <h4 style={{ marginBottom: '8px', fontSize: '16px', fontWeight: 600 }}>
                  {module.title}
                </h4>
                <p style={{ color: '#666', fontSize: '14px', margin: 0 }}>
                  {module.description}
                </p>
              </div>
            </Card>
          </Col>
        ))}
      </Row>
    </div>
  );
};

export default AdminDashboard;
