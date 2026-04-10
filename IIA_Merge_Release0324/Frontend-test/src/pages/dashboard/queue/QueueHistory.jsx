import { Modal, Table, Spin, Tag } from 'antd';
import { useEffect, useState } from 'react';
import axios from 'axios';
import { HistoryOutlined } from '@ant-design/icons';

const QueueHistory = ({ requestId, open, onCancel }) => {
  const [history, setHistory] = useState([]);
  const [loading, setLoading] = useState(false);

  const columns = [
    {
      title: 'Stage',
      dataIndex: 'currentRole',
      key: 'currentRole',
      render: (text) => <Tag color="processing">{text}</Tag>,
    },
    {
      title: 'Action',
      dataIndex: 'action',
      key: 'action',
      render: (text) => {
        if (text === 'Auto-Approved' || text === 'AUTO_APPROVED') {
          return <Tag color="orange">Auto-Approved</Tag>;
        }
        return (
          <Tag color={text === 'APPROVED' ? 'green' : 'geekblue'}>
            {text.toLowerCase()}
          </Tag>
        );
      },
    },
    {
      title: 'Routed To',
      dataIndex: 'assignedToEmployeeName',
      key: 'assignedTo',
      render: (name, record) => {
        const role = record.nextRole || '';
        if (!name && !record.assignedToUserId) return `${role}`;
        
        return name ? `${name}${role ? ` (${role})` : ''}` : '--';
      },
    },
    {
      title: 'Remarks',
      dataIndex: 'remarks',
      key: 'remarks',
      render: (text) => text || '--',
    },
    {
      title: 'Date',
      dataIndex: 'createdDate',
      key: 'createdDate',
      render: (text) => new Date(text).toLocaleDateString('en-IN', {
        day: '2-digit',
        month: 'short',
        year: 'numeric',
        hour: '2-digit',
        minute: '2-digit'
      }),
    },
  ];

  useEffect(() => {
    const fetchHistory = async () => {
      if (open && requestId) {
        setLoading(true);
        try {
          const response = await axios.get(
            `/workflowTransitionHistory?requestId=${requestId}`
          );
          
          // Access responseData array from the API response
          const historyData = Array.isArray(response?.data?.responseData) 
            ? response.data.responseData 
            : [];
            
          setHistory(historyData);
          
        } catch (error) {
          console.error('Error fetching history:', error);
          setHistory([]);
        } finally {
          setLoading(false);
        }
      }
    };

    fetchHistory();
  }, [open, requestId]);

  return (
    <Modal
      title={
        <div className="flex items-center gap-2">
          <HistoryOutlined />
          Workflow History for {requestId}
        </div>
      }
      open={open}
      onCancel={onCancel}
      footer={null}
      width={800}
    >
      <Spin spinning={loading}>
        <Table
          dataSource={history}
          columns={columns}
          rowKey="workflowTransitionId"
          pagination={false}
          scroll={{ x: true }}
          bordered
          size="small"
          locale={{
            emptyText: 'No workflow history found',
          }}
        />
      </Spin>
    </Modal>
  );
};

export default QueueHistory;