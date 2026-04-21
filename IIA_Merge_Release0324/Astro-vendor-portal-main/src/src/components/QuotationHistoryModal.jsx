// components/QuotationHistoryModal.jsx
import { Modal, Table, Spin, Tag } from 'antd';
import { useEffect, useState } from 'react';
import axios from 'axios';
import { HistoryOutlined } from '@ant-design/icons';

const QuotationHistoryModal = ({ tenderId, vendorId, open, onClose }) => {
  const [history, setHistory] = useState([]);
  const [loading, setLoading] = useState(false);

  const columns = [
    {
  title: 'Status',
  dataIndex: 'status',
  key: 'status',
  render: (text) => {
    let color =
      text === 'Rejected'
        ? 'red'
        : text === 'CHANGE_REQUESTED'
        ? 'orange'
        : 'green';

    let label = text === 'CHANGE_REQUESTED'
      ? 'Clarification sought'
      : text;

    return <Tag color={color}>{label}</Tag>;
  }
},
    {
      title: 'Remarks',
      dataIndex: 'remarks',
      key: 'remarks',
      render: (text) => text || '--',
    },
    {
      title: 'Date',
      dataIndex: 'date',
      key: 'date',
      render: (text) =>
        text ? new Date(text).toLocaleString('en-IN') : '--',
    },
  ];

  useEffect(() => {
    if (open && tenderId && vendorId) {
      setLoading(true);
      axios
        .get(`/api/vendor-quotation/vendorHistory/${tenderId}/${vendorId}`)
        .then((res) => {
          const data = res.data?.responseData || [];
          setHistory(data);
        })
        .catch((err) => {
          console.error("Error fetching quotation history:", err);
          setHistory([]);
        })
        .finally(() => setLoading(false));
    }
  }, [open, tenderId, vendorId]);

  return (
    <Modal
      open={open}
      onCancel={onClose}
      footer={null}
      width={700}
      title={
        <span>
          <HistoryOutlined /> Quotation History for Vendor {vendorId}
        </span>
      }
    >
      <Spin spinning={loading}>
        <Table
          columns={columns}
          dataSource={history}
          rowKey="date"
          size="small"
          bordered
          pagination={false}
          locale={{ emptyText: 'No quotation history found.' }}
        />
      </Spin>
    </Modal>
  );
};

export default QuotationHistoryModal;
