import React, { useEffect, useState, useCallback } from "react";
import { Card, Row, Col, message, Spin ,Button, Tag} from "antd";
import { useSelector } from "react-redux";
import { useParams } from "react-router-dom";
import axios from "axios";
import TenderEvaluator from "../../../components/Tender_Evaluator";
import PurchaseOrderDetails from "../../../components/Purchaseorder_details";
import { Modal } from "antd";
import QuotationHistoryModal from '../../../components/QuotationHistoryModal';
import { HistoryOutlined } from '@ant-design/icons';
import AllVendorsQuotationsstatus from '../../../components/AllVendorsQuotationstatus';


const mapUserIdToRole = (userId) => {
  // Replace with real user lookup if needed.
  switch (userId) {
    case 18:
      return "Indent Creator";
    case 17:
      return "Reporting Officer";
    // Add other known mappings here, e.g., SPO etc.
    default:
      return `${userId}`;
  }
};
const Form2 = () => {
  const { vendorId } = useParams();
  const auth = useSelector((state) => state.auth);
  const [tenderIds, setTenderIds] = useState([]);
  const [selectedTenderId, setSelectedTenderId] = useState(null);
  const [loading, setLoading] = useState(true);
  const [selectedTenderLoading, setSelectedTenderLoading] = useState(false);
  const [historyVisible, setHistoryVisible] = useState(false);
  const [allVendorVisible, setAllVendorVisible] = useState(false);


  const [vendorState, setVendorState] = useState({
    qualified: false,
    changeRequest: false,
    remarks: "",
    actionTakenBy: null,
    actionStatus: null,
    vendorIds:[],
    povendorId:null,
    actionStatusAfterPoGenerated:null,
    approvedVendorPoData:null,
  });
  // Modified by Aman 
  const [vendorInfo, setVendorInfo] = useState({ vendorId: "", vendorName: "", primaryBusiness: "" });
  // End
  useEffect(() => {
    const fetchTenderIds = async () => {
      try {
        const res = await axios.get(
          `/api/vendor-master/approvedtenderIDs/${vendorId}`,
          {
            headers: {
              "Content-Type": "application/json",
              Authorization: `Bearer ${auth.token}`,
            },
          }
        );
        setTenderIds(res.data.responseData || []);
        // Modified by Aman 
        setVendorInfo(res.data.vendorData );
        // End 
      } catch (err) {
        console.error("Failed to fetch tender IDs:", err);
        message.error("Could not fetch tender IDs: " + (err.message || ""));
      } finally {
        setLoading(false);
      }
    };
    if (vendorId) fetchTenderIds();
  }, [vendorId, auth.token]);

  const handleTenderCardClick = useCallback(
    async (tenderId) => {
      setSelectedTenderId(tenderId);
      setSelectedTenderLoading(true);
      try {
        const res = await axios.get(
          `/api/tender-requests/vendor/${tenderId}/${vendorId}`,
          {
            headers: {
              "Content-Type": "application/json",
              Authorization: `Bearer ${auth.token}`,
            },
          }
        );
        const responseData = res.data?.responseData || {};
        const {
          qualified = false,
          changeRequest = false,
          remarks = "",
          actionTakenBy = null,
          actionStatus = null,
          vendorIds=[],
          povendorId=null,
          actionStatusAfterPoGenerated=null,
          approvedVendorPoData=null,
        } = responseData;

        setVendorState({
          qualified,
          changeRequest,
          remarks,
          actionTakenBy,
          actionStatus,
          vendorIds,
          povendorId,
          actionStatusAfterPoGenerated,
          approvedVendorPoData,
        });
      } catch (err) {
        console.error("Vendor check failed:", err);
        message.error("Failed to check vendor: " + (err.message || ""));
        setVendorState({
          qualified: false,
          changeRequest: false,
          remarks: "",
          actionTakenBy: null,
          actionStatus: null,
          vendorIds:[],
          povendorId:null,
          actionStatusAfterPoGenerated,
          approvedVendorPoData,
        });
      } finally {
        setSelectedTenderLoading(false);
      }
    },
    [vendorId, auth.token]
  );

  if (loading) return <Spin tip="Loading Tender IDs..." />;

  const {
    qualified,
    changeRequest,
    remarks,
    actionTakenBy,
    actionStatus,
    vendorIds,
    povendorId,
    actionStatusAfterPoGenerated,
    approvedVendorPoData,
  } = vendorState;

const isChangeRequest = actionStatus === "CHANGE_REQUESTED";
const isChangeRequestToIndentor = actionStatus === "CHANGE_REQUESTED_TO_INTENTOR";


  const isSubmittedOrNone =
    actionStatus === "SUBMITTED" || actionStatus === null || actionStatus === undefined;
  const isAccepted = actionStatus === "ACCEPTED";
  const isRejected = actionStatus === "REJECTED";


const getDisplayStatus = (actionStatus) => {
  if (!actionStatus) return "Pending";

  switch (actionStatus.toUpperCase()) {
    case "ACCEPTED":
      return "Qualified";
    case "REJECTED":
      return "Disqualified";
    default:
      return actionStatus; 
  }
};

  return (
    <div style={{ padding: "20px" }}>
    {/* Modified by Aman  */}
<div style={{ textAlign: "center", marginBottom: "16px" }}>
  <span style={{ color: "#40a9ff", fontSize: "42px", fontWeight: "bold", display: "block" }}>
    Indian Institute of Astrophysics
  </span>
</div>
    <div style={{ display: "flex", justifyContent: "space-between", marginBottom: "16px", fontWeight: "600" }}>
  <span>Vendor ID: {vendorInfo.vendorId}</span>
  <span>Vendor Name: {vendorInfo.vendorName}</span>
  <span>Primary Business: {vendorInfo.primaryBusiness}</span>
</div>
{/* End */}
      <h2 className="font-bold mb-2">Approved Tender IDs</h2>
      <Row gutter={[16, 16]}>
        {/*tenderIds.map((tenderId) => (
          <Col key={tenderId} xs={24} sm={12} md={8} lg={6}>
            <Card
              hoverable
              style={{
                textAlign: "center",
                cursor: "pointer",
                border:
                  selectedTenderId === tenderId
                    ? "2px solid #1890ff"
                    : undefined,
              }}
              onClick={() => handleTenderCardClick(tenderId)}
            >
              {selectedTenderLoading && selectedTenderId === tenderId ? (
                <Spin />
              ) : (
                <a>{tenderId}</a>
              )}
            </Card>
          </Col>
        ))*/}
        {tenderIds.map((tender) => (
          <Col key={tender.tenderId} xs={24} sm={12} md={8} lg={6}>
            <Card
              hoverable
              style={{
              textAlign: "center",
              cursor: "pointer",
              border:
                selectedTenderId === tender.tenderId ? "2px solid #1890ff" : undefined,
              }}
              onClick={() => handleTenderCardClick(tender.tenderId)}
             >
          {selectedTenderLoading && selectedTenderId === tender.tenderId ? (
          <Spin />
            ) : (
            <>
              <a style={{ color: "inherit", fontWeight: "semi bold" }}>
              {tender.tenderId}
              </a>
              <div style={{ marginTop: 4, color: "#555" }}>{tender.title}</div>
            </>
             )}
          </Card>
       </Col>
      ))}


      </Row>
      {/*selectedTenderId && (
  <>
    <Button
      type="link"
      icon={<HistoryOutlined />}
      onClick={() => setHistoryVisible(true)}
      style={{ marginTop: 20 }}
    >
      View Quotation History
    </Button>

    <QuotationHistoryModal
      open={historyVisible}
      onClose={() => setHistoryVisible(false)}
      tenderId={selectedTenderId}
      vendorId={vendorId}
    />
  </>
)*/}
{selectedTenderId && (
  <>
    <Button
      type="link"
      icon={<HistoryOutlined />}
      onClick={() => setHistoryVisible(true)}
      style={{ marginTop: 20 }}
    >
      View Quotation History
    </Button>

    <Button
      type="link"
      icon={<HistoryOutlined />}
      onClick={() => setAllVendorVisible(true)}
      style={{ marginTop: 20, marginLeft: 10 }}
    >
      View All Vendors Status
    </Button>

    {/* Existing Quotation History */}
    <QuotationHistoryModal
      open={historyVisible}
      onClose={() => setHistoryVisible(false)}
      tenderId={selectedTenderId}
      vendorId={vendorId}
    />

    {/* New All Vendors Status Modal */}
    <AllVendorsQuotationsstatus
      open={allVendorVisible}
      onClose={() => setAllVendorVisible(false)}
      tenderId={selectedTenderId}
      vendorId={vendorId}
    />
  </>
)}


      {/* Change Request Banner */}
      {selectedTenderId && isChangeRequest && (
        <div
          style={{
            marginTop: 24,
            padding: "12px 16px",
            background: "#fffbe6",
            border: "1px solid #ffe58f",
            borderRadius: 4,
            display: "flex",
            gap: 8,
            alignItems: "center",
          }}
        >
          <strong style={{ marginRight: 8 }}>Clarification sought</strong>
        { /* <span>
            by <em>{mapUserIdToRole(actionTakenBy)}</em>
          </span>*/}
          {/*actionTakenBy && (
            <span>
              by <em>{actionTakenBy}</em>
            </span>
          )*/}
          {remarks && (
            <span>
              — <strong>Remarks:</strong> {remarks}
            </span>
          )}
        </div>
      )}

      {/* Show TenderEvaluator when change requested, submitted, or no action yet */}
      {selectedTenderId && (isChangeRequest || isSubmittedOrNone) && (
        <div style={{ marginTop: "40px" }}>
          <TenderEvaluator key={selectedTenderId} tenderId={selectedTenderId}  actionStatus={actionStatus}/>
        </div>
      )}

      {/* Show PurchaseOrderDetails when fully qualified & accepted (and not change requested) */}
      {/*selectedTenderId && qualified && !changeRequest && isAccepted && (
        <div style={{ marginTop: "40px" }}>
          <PurchaseOrderDetails
            key={selectedTenderId}
            tenderId={selectedTenderId}
          />
        </div>
      )*/}
       {/* Purchase Order: only when Store Purchase Officer did VENDOR QULIFIED */}
    {selectedTenderId &&
  actionTakenBy === "Store Purchase Officer" && (
    <div style={{ marginTop: "40px" }}>
      {actionStatus === "PO Completed" && (
        <>
          <p
            style={{
              fontWeight: "bold",
              marginBottom: "10px",
              color: "green",
            }}
          >
            Vendor quotation for Tender ID {selectedTenderId} is Accepted.
            PO generated with details
          </p>
          <PurchaseOrderDetails
            key={selectedTenderId}
            tenderId={selectedTenderId}
          />
        </>
      )}

      {/*actionStatus === "PO Raised" && (
        <p
          style={{
            fontWeight: "bold",
            marginBottom: "10px",
            color: "green",
          }}
        >
          PO Proposed and approval in progress
        </p>
      )*/}
     {actionStatus === "PO Raised" && (
  <>
    {actionStatusAfterPoGenerated?.toLowerCase() === "rejected" ? (
      <div style={{ marginTop: 8, fontWeight: "bold" }}>
        <p>
          Vendor quotation for Tender ID {selectedTenderId} is Disqualified.
        </p>
        <p>
          Reason For Rejection: {remarks || "No remarks provided"}
        </p>
        {approvedVendorPoData === "PO Completed" ? (
      <>
        <p style={{ color: "green" }}>
        Purchase order has been Generated for Vendor ID: {povendorId} and Approval process is Completed
        </p>
      {/* <div style={{ marginTop: 12 }}>
        <PurchaseOrderDetails
          key={selectedTenderId}
          tenderId={selectedTenderId}
        />
    </div>*/}
      </>
    ) : (
      <p style={{ color: "green" }}>
       Purchase order has been proposed for Vendor ID: {povendorId} and approval is in progress.
      </p>
    )}
        
      </div>
    ) : auth.vendorId === povendorId ? (
      <p
        style={{
          fontWeight: "bold",
          marginBottom: "10px",
          color: "green",
        }}
      >
        Vendor quotation for Tender ID {selectedTenderId} is Accepted.
        PO Proposed and approval in progress
      </p>
    ) : (
      <>
        <p
          style={{
            fontWeight: "bold",
            marginBottom: "10px",
           // color: "orange",
          }}
        >
          Vendor quotation is qualified but the Purchase Order has not been proposed.
        </p>
       {approvedVendorPoData === "PO Completed" ? (
        <>
          <p style={{ 
             fontWeight: "bold",
            marginBottom: "10px",
          //  color: "green"
            }}>
          Purchase order has been generated for Vendor ID: {povendorId} and approval process is Completed
          </p>
       { /*  <div style={{ marginTop: 12 }}>
            <PurchaseOrderDetails
            key={selectedTenderId}
            tenderId={selectedTenderId}
          />
          </div>*/}
        </>
      ) : (
      <p style={{ 
       // color: "green" 
        fontWeight: "bold",
        marginBottom: "10px",
        }}>
       Purchase Order has been Proposed for Vendor ID: {povendorId} and approval is in progress.
       </p>
     )}



      </>
    )}
  </>
)}



      {actionStatus === "VENDOR QULIFIED" &&
        actionStatus !== "PO Completed" &&
        actionStatus !== "PO Raised" && (
          <PurchaseOrderDetails
            key={selectedTenderId}
            tenderId={selectedTenderId}
          />
        )}
    </div>
)}



      {/* Show rejection reason when explicitly rejected or unqualified (excluding change request) */}
 {selectedTenderId && (actionStatus === "ACCEPTED" || actionStatus === "REJECTED") && (
  <div style={{ marginTop: "40px" }}>
    <div
      style={{
        padding: 24,
        background: actionStatus === "ACCEPTED" ? "#e6ffed" : "#fff3f0",
        border: actionStatus === "ACCEPTED"
          ? "1px solid #b7eb8f"
          : "1px solid rgb(218, 200, 199)",
        borderRadius: 4,
      }}
    >
    {actionTakenBy === "Indent Creator" || actionTakenBy === "Purchase personnel" && (
      <strong>
        Vendor quotation for Tender ID {selectedTenderId} is {getDisplayStatus(actionStatus)} for further tender evaluation process.
      </strong>
    )}
     {actionTakenBy === "Store Purchase Officer" && (
      <strong>
        Vendor quotation for Tender ID {selectedTenderId} is {getDisplayStatus(actionStatus)}.
      </strong>
    )}


      {actionStatus === "REJECTED" && remarks && (
        <div style={{ marginTop: 8 }}>
          <span>
            <strong>Reason For Rejection:</strong> {remarks}
          </span>
        </div>
      )}

      {/* For Indentor or Purchase Personnel */}
      {/*(actionTakenBy === "Purchase personnel" || actionTakenBy === "Indent Creator") && (
        <div style={{ marginTop: 8 }}>
          <span>Tender Evaluation in progress</span>
        </div>
      )*/}

      {/* For Store Purchase Officer */}
      {/*actionTakenBy === "Store Purchase Officer" && (
        <div style={{ marginTop: 8 }}>
          <span>
            {actionStatus === "ACCEPTED"
              ? "Purchase order pending to be proposed"
              : "Purchase order pending to be proposed"}
          </span>
        </div>
      )*/}
      {actionTakenBy === "Store Purchase Officer" && (
  <div style={{ marginTop: 8 }}>
    <span>
      {actionStatus === "ACCEPTED"
        ? "Purchase order pending to be proposed"
        : actionStatus === "REJECTED"
        ? `Purchase order has not yet been generated for the Qualified Vendors.: ${Array.isArray(vendorIds) ? vendorIds.join(", ") : ""}`
        : "Purchase order pending to be proposed"}
    </span>
  </div>
)}
    </div>
  </div>
)}



  {/* Special red box when Store Purchase Officer accepted but PO not raised 
      {selectedTenderId &&
        actionTakenBy === "Store Purchase Officer" &&
        actionStatus === "ACCEPTED" && (
          <div style={{ marginTop: "40px" }}>
            <div
              style={{
                padding: 24,
                background: "#fff3f0",
                border: "1px solid rgb(218, 200, 199)",
                borderRadius: 4,
              }}  >
              <strong>
                Store Purchase Officer accepted the vendor but PO is not raised on this vendor for Tender ID {selectedTenderId}.
              </strong>
              {remarks && (
                <div style={{ marginTop: 8 }}>
                  <span>
                    <strong>Remarks:</strong> {remarks}
                  </span>
                </div>
                 )}
            </div>
          </div>
        )}*/}
{/* Red box for CHANGE_REQUESTED_TO_INTENTOR */}
{selectedTenderId && isChangeRequestToIndentor && (
  <div style={{ marginTop: "40px" }}>
    <div
      style={{
        padding: 24,
        background: "#fff3f0",
        border: "1px solid rgb(218, 200, 199)",
        borderRadius: 4,
      }}
    >
      <strong>
        Vendor quotation for Tender ID {selectedTenderId} is {actionStatus === "CHANGE_REQUESTED_TO_INTENTOR"
          ? "Clarification Sought to Indentor"
          : actionStatus}.
      </strong>
      {remarks && (
        <div style={{ marginTop: 8 }}>
          <span>
            <strong>Remarks:</strong> {remarks}
          </span>
        </div>
      )}
      <div style={{ marginTop: 8 }}>
          <span>Tender Evaluation in progress</span>
      </div>
      {/*actionTakenBy && (
        <div style={{ marginTop: 4 }}>
          <span>
            <strong>Action taken by:</strong> {mapUserIdToRole(actionTakenBy)}
          </span>
        </div>
      )*/}
    </div>
  </div>
)}



    </div>
  );
};

export default Form2;