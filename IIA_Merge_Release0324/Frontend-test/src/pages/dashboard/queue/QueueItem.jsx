// QueueItem.js
import React, { useState } from "react";
import { Card, Button, Input, Select, Space, Typography } from "antd";
import { UserOutlined, CommentOutlined } from "@ant-design/icons";

const { Text } = Typography;
const { TextArea } = Input;

const QueueItem = ({ item }) => {
  const [rejectCommentsVisible, setRejectCommentsVisible] = useState(false);
  const [additionalInfoVisible, setAdditionalInfoVisible] = useState(false);
  const [requestChangeVisible, setRequestChangeVisible] = useState(false);
  const [selectedUser, setSelectedUser] = useState(null);
  const [comments, setComments] = useState("");

  const handleApprove = () => {
    ;
  };

  const handleReject = () => {
    ;
    setRejectCommentsVisible(!rejectCommentsVisible);
  };

  const handleAdditionalInfo = () => {
    setAdditionalInfoVisible(!additionalInfoVisible);
  };

  const handleRequestChange = () => {
    setRequestChangeVisible(!requestChangeVisible);
  };

  const handleUserSelect = (value) => {
    setSelectedUser(value);
  };

  const handleCommentChange = (e) => {
    setComments(e.target.value);
  };

  return (
    <Card
      title={`Request ID: ${item.requestId}`}
      style={{ marginBottom: 16 }}
      extra={
        <Space>
          <Button type="primary" onClick={handleApprove}>
            Approve
          </Button>
          <Button danger onClick={handleReject}>
            Reject
          </Button>
          <Button icon={<CommentOutlined />} onClick={handleAdditionalInfo}>
            Additional Info
          </Button>
          <Button onClick={handleRequestChange}>Seek Clarification</Button>
        </Space>
      }
    >
      <Space direction="vertical" style={{ width: "100%" }}>
        <Text strong>Workflow Type: {item.workflowType}</Text>
        <Text>Indent ID: {item.indentId}</Text>
        <Text>CP ID: {item.cpId}</Text>
        <Text>Tender ID: {item.tenderId}</Text>
        <Text>PO/SO/WO ID: {item.poSoWoId}</Text>

        {rejectCommentsVisible && (
          <TextArea
            placeholder="Enter reject comments"
            rows={2}
            style={{ marginTop: 8 }}
          />
        )}

        {additionalInfoVisible && (
          <Space direction="vertical" style={{ width: "100%", marginTop: 8 }}>
            <Select
              placeholder="Select User"
              style={{ width: "100%" }}
              onChange={handleUserSelect}
              suffixIcon={<UserOutlined />}
            >
              <Select.Option value="user1">User 1</Select.Option>
              <Select.Option value="user2">User 2</Select.Option>
              <Select.Option value="user3">User 3</Select.Option>
            </Select>
            <TextArea
              placeholder="Enter additional comments"
              rows={2}
              value={comments}
              onChange={handleCommentChange}
            />
          </Space>
        )}

        {requestChangeVisible && (
          <TextArea
            placeholder="Enter request change comments"
            rows={2}
            style={{ marginTop: 8 }}
          />
        )}
      </Space>
    </Card>
  );
};

export default QueueItem;