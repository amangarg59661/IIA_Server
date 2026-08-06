import React, { useEffect, useState } from 'react';
import { Card, Descriptions, Spin, message ,Button} from 'antd';
import FormContainer from './DKG_FormContainer';
import Heading from './DKG_Heading';
import axios from 'axios';
import { useSelector } from 'react-redux';
import { baseURL } from '../App';


const PurchaseOrderDetails = ({ tenderId }) => {
  const [orderData, setOrderData] = useState(null);
  const [loading, setLoading] = useState(true);
 const vendorId = useSelector((state) => state.auth.vendorId);
   const token = useSelector((state) => state.auth.token);
  // useEffect(() => {
  //   const fetchPurchaseOrderDetails = async () => {
  //     try {
  //       const res = await axios.get(`/api/vendor-master/approvedVendorData`,{params: {tenderID:tenderId}});
  //       if (res.data.responseStatus.statusCode === 0) {
  //         setOrderData(res.data.responseData);
  //       } else {
  //         message.error('Failed to fetch purchase order details');
  //       }
  //     } catch (error) {
  //       console.error('Error fetching purchase order details:', error);
  //       message.error('An error occurred while fetching purchase order details');
  //     } finally {
  //       setLoading(false);
  //     }
  //   };

  //   fetchPurchaseOrderDetails();
  // }, [tenderId]);
  useEffect(() => {
  const fetchPurchaseOrderDetails = async () => {
    try {
      const res = await axios.get(`/api/vendor-master/approvedVendorData`, {
        params: { tenderID: tenderId },
        headers: { Authorization: `Bearer ${token}` },
      });
      if (res.data.responseStatus.statusCode === 0) {
        setOrderData(res.data.responseData);
      } else {
        message.error('Failed to fetch purchase order details');
      }
    } catch (error) {
      console.error('Error fetching purchase order details:', error);
      message.error('An error occurred while fetching purchase order details');
    } finally {
      setLoading(false);
    }
  };

  fetchPurchaseOrderDetails();
}, [tenderId]);

  if (loading) return <Spin tip="Loading Purchase Order..." />;

  if (!orderData) {
    return (
      <FormContainer>
        <Heading title="Purchase Order Details" />
        <p>vendor quotation is qualified, but the purchase order has not been generated yet.</p>
      </FormContainer>
    );
  }
//   const handleOpenTenderFormat = () => {
//   const url = `${baseURL}/data/tender-format?tenderId=${tenderId}&vendorId=${vendorId}`;
//   window.open(url, '_blank');
// };


// const handleOpenPoFormat = () => {
//    const poId = 'PO' + tenderId.substring(1);
//   const url = `${baseURL}/data/po-format?poId=${poId}`;
//   window.open(url, '_blank');
// };
const handleOpenTenderFormat = async () => {
  try {
    const response = await axios.get(`/data/tender-format`, {
      params: { tenderId, vendorId }, // dropped bogus tenderVersion
      headers: { Authorization: `Bearer ${token}` },
      responseType: 'blob',
    });
    const fileURL = URL.createObjectURL(response.data);
    window.open(fileURL, "_blank");
  } catch (err) {
    console.error("Failed to open tender format:", err);
    message.error("Could not load tender format");
  }
};

const handleOpenPoFormat = async () => {
  try {
    const poId = 'PO' + tenderId.substring(1);
    const response = await axios.get(`/data/po-format`, {
      params: { poId }, // single source of query param now
      headers: { Authorization: `Bearer ${token}` },
      responseType: 'blob',
    });
    const fileURL = URL.createObjectURL(response.data);
    window.open(fileURL, "_blank");
  } catch (err) {
    console.error("Failed to open po format:", err);
    message.error("Could not load po format");
  }
};
   

  const {
    tenderNumber,
    purchaseOrder,
    deliveryAndAcceptanceStatus,
    paymentStatus,
    paymentUTRNumber,
    date,
    tenderRequestCopy,
    poCopy,
  } = orderData;

 return (
  <FormContainer>
    <Heading title={`Purchase Order for Tender ID: ${tenderNumber}`} />
    <div style={{ padding: '24px' }}>
      <div>
        <Descriptions
          column={1}
          bordered
          size="middle"
          labelStyle={{ fontWeight: '600', backgroundColor: '#fafafa', width: '30%' }}
          contentStyle={{ backgroundColor: '#fff' }}
        >
          <Descriptions.Item label="Tender Number">{tenderNumber || 'N/A'}</Descriptions.Item>
          <Descriptions.Item label="Purchase Order">{purchaseOrder || 'N/A'}</Descriptions.Item>
          <Descriptions.Item label="Delivery & Acceptance Status">
            {deliveryAndAcceptanceStatus !== 'null' ? deliveryAndAcceptanceStatus : 'N/A'}
          </Descriptions.Item>
          <Descriptions.Item label="Payment Status">
            {paymentStatus !== 'null' ? paymentStatus : 'N/A'}
          </Descriptions.Item>
          <Descriptions.Item label="Payment UTR Number">
            {paymentUTRNumber !== 'null' ? paymentUTRNumber : 'N/A'}
          </Descriptions.Item>
          <Descriptions.Item label="Date">
            {date ? new Date(date).toLocaleDateString() : 'N/A'}
          </Descriptions.Item>
          <Descriptions.Item label="Tender Request Copy">
           <Button onClick={handleOpenTenderFormat}>View Tender Copy</Button>
          </Descriptions.Item>
          <Descriptions.Item label="PO Copy">
             <Button onClick={handleOpenPoFormat}>View Po Copy</Button>
          </Descriptions.Item>
        </Descriptions>
      </div>
    </div>
  </FormContainer>
);
}

export default PurchaseOrderDetails;
