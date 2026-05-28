// components/QuotationHistoryModal.jsx
import { Modal, Table, Spin, Tag } from 'antd';
import { useEffect, useState } from 'react';
import axios from 'axios';
import { HistoryOutlined } from '@ant-design/icons';

const AllVendorsQuotationsstatus = ({ tenderId, vendorId, open, onClose }) => {
  const [history, setHistory] = useState([]);
  const [loading, setLoading] = useState(false);

  const columns = [
     {
      title: 'vendorId',
      dataIndex: 'vendorId',
      key: 'vendorId',
      render: (text) => text || '--',
    },
     {
      title: 'Vendor Name',
      dataIndex: 'vendorName',
      key: 'vendorName',
      render: (text) => text || '--',
    },
    {
  title: 'Status',
  dataIndex: 'status',
  key: 'status',
  render: (text) => {
    let color =
      text === 'Disqualified'
        ? 'red'
        : text === 'Disqualified'
        ? 'orange'
        : 'green';

    let label = text === 'CHANGE_REQUESTED'
      ? 'Clarification sought'
      : text;

    return <Tag color={color}>{label}</Tag>;
  }
},
    {
      title: 'Po Status',
      dataIndex: 'po',
      key: 'po',
      render: (text) => text || '--',
    },
  ];

  useEffect(() => {
    if (open && tenderId && vendorId) {
      setLoading(true);
      axios
        .get(`/api/vendor-quotation/all-vendors/Status/${tenderId}`)
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
          <HistoryOutlined />All Vendors status 
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

export default AllVendorsQuotationsstatus;
