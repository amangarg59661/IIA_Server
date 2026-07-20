import React from "react";
import { BrowserRouter, Route, Routes } from "react-router-dom";
import Login from "../auth/Login";
import ChangePassword from "../auth/ChangePassword"; // TC_14: First login password change
import PageNotFound from "../pageNotFound/PageNotFound";
import Form3 from "../dashboard/indentModification/Form3";
import Form17 from "../dashboard/goodsTransfer/Form17";
import Form20 from "../dashboard/demandAndIssue/Form20";
import Form10 from "../dashboard/deliveryTracking/Form10";
import QueueTable from "../dashboard/queue/QueueTable";
import Form4a from "../dashboard/tenderRequest/Form4a";
import ReportsMain from "../reports/ReportsMain";
import CpReport from "../reports/CpReport";
import IndentReport from "../reports/IndentReport";
import TechnoMom from "../reports/TechnoMom";
import VendorContract from "../reports/VendorContractReport";
import ProcurementActivityReport from "../reports/ProcurementActivityReport";
import MainDashboard from "../dashboard/newDashboard/MainDashboard";
import GPRN from "../dashboard/goodsProvisionalRecieptNote/GPRN";
import GoodsInspection from "../dashboard/goodsInspection/GoodsInspection";
import JobCreation from "../dashboard/jobCreation/JobCreation";
import WorkCreation from "../dashboard/workCreation/WorkCreation";
import Master from "../masters/Master";
import Grv from "../dashboard/grv/Grv";
import Grn from "../dashboard/grn/Grn";
import Isn from "../dashboard/isn/Isn";
import Ogp from "../dashboard/ogp/Ogp";
import Igp from "../dashboard/igp/Igp";
import Asset from "../dashboard/asset/Asset";
import AssetDisposal from "../dashboard/assetDisposal/AssetDisposal";
import InvReportsMain from "../reports/InvReportsMain";
import GoodsIssueReport from "../reports/GoodsIssueReport";
import IgpReport from "../reports/IgpReport";
import OgpReport from "../reports/OgpReport";
import AssetReport from "../reports/AssetReport";
import StockReport from "../reports/StockReport";
import Tender from "../dashboard/tenderRequest/Tender";
import ContingencyPurchase from "../dashboard/contingencyPurchase/ContingencyPurchase";
import PO from "../dashboard/purchaseOrder/PO";
import SO from "../dashboard/serviceOrder/SO";
import Indent1 from "../dashboard/indentCreation/Indent1";
import CustomLayout from "../../components/DKG_CustomLayout";
import Quotations from '../dashboard/tenderRequest/Quotations';
import PoList from '../reports/PoList';
import SoList from '../reports/SoList';
import PoStatus from '../reports/PoStatus';
import SoStatus from "../reports/SoStatus";
import IndentList from "../reports/IndentList";
import QuarterlyVigilanceSoReport from '../reports/QuarterlyVigilanceSoReport';
import ShortClosedCancelledOrderReport from "../reports/ShortClosedCancelledOrderReport";
import MonthlyProcurementReport from "../reports/MonthlyProcurementReport";
import IndentStatus from '../reports/IndentStatus'
import TenderEvaluator from "../dashboard/tenderRequest/TenderEvaluator";
import TenderEvaluatorGem from "../dashboard/tenderRequest/TenderEvaluatorGem";
import TenderEvaluationPage from "../dashboard/tenderEvaluation/TenderEvaluationPage";
import GemTenderEvaluationPage from "../dashboard/tenderEvaluation/GemTenderEvaluationPage";
import ForDisposalAssets from "../dashboard/assetDisposal/ForDisposalAssets";
import { useSelector } from "react-redux";
import ProtectedRoute from "./ProtectedRoute";
import Invoice from "../dashboard/PaymentVoucher/Invoice";
import AccountingDashboard from "../accounting/AccountingDashboard";
import VendorLedger from "../accounting/VendorLedger";
import TrialBalance from "../accounting/TrialBalance";
import PaymentRegister from "../accounting/PaymentRegister";
import TallyIntegrationReport from "../reports/TallyIntegrationReport"
import CancellationApprovalPage from "../dashboard/cancellation/CancellationApprovalPage";
import AdminDashboard from "../dashboard/admin/AdminDashboard";
import ListOfValues from "../dashboard/admin/ListOfValues";
import ApprovalWorkflow from "../dashboard/admin/ApprovalWorkflow";
import ProjectManagement from "../dashboard/admin/ProjectManagement";
import BudgetManagement from "../dashboard/admin/BudgetManagement";
import EmployeeRegistration from "../dashboard/admin/EmployeeRegistration";
import UserCreation from "../dashboard/admin/UserCreation";
import ApprovalLimitsConfig from "../dashboard/admin/ApprovalLimitsConfig";
import DepartmentApproverMapping from "../dashboard/admin/DepartmentApproverMapping";
import FieldStationApproverConfig from "../dashboard/admin/FieldStationApproverConfig";
import FullWorkflowConfig from "../dashboard/admin/FullWorkflowConfig";
import CommitteeManagement from "../dashboard/admin/CommitteeManagement";
/*
const RoutesComponent = () => {
  return (
    <BrowserRouter>
      <Routes>
          <Route path="/" element={<CustomLayout />}>
            <Route index element={<MainDashboard />} />
            <Route path="/queue" element={<QueueTable />} />
            <Route path="/masters" element={<Master />} />

            <Route path="/procurement">
              <Route path="indent">
                <Route path="creation" element={<Indent1 />} />
                <Route path="modification" element={<Form3 />} />
              </Route>
              <Route path="tender">
                <Route path="request" element={<Tender />} />
               <Route path="evaluationn" element={<Form4a />} />
               <Route path="evaluation" element={<TenderEvaluator />} />
               <Route path="gem" element={<TenderEvaluatorGem />} />
               <Route path="/procurement/tender/Quotations" element={<Quotations />} />
              </Route>
              <Route path="purchaseOrder" element={<PO />} />
              <Route path="serviceOrder" element={<SO />} />
              <Route path="contingencyPurchase" element={<ContingencyPurchase />} />
              <Route path="jobCreation" element={<JobCreation />} />
              <Route path="workCreation" element={<WorkCreation />} />
              <Route path="deliveryTracking" element={<Form10 />} />
            </Route>

            <Route path="/reports" element={<ReportsMain />}>
              <Route path="cpReport" element={<CpReport />} />
              <Route path="indentReport" element={<IndentReport />} />
              <Route path="technoMom" element={<TechnoMom />} />
              <Route path="vendorContract" element={<VendorContract />} />
              <Route path="procurementActivity" element={<ProcurementActivityReport />} />
              <Route path="PoList" element={<PoList />} />
              <Route path="SoList" element={<SoList />} />
              <Route path="PoStatus" element={<PoStatus />} />
              <Route path="SoStatus" element={<SoStatus />} />
              <Route path="IndentList" element={<IndentList />} />
              <Route path="QuarterlyVigilanceSoReport" element={<QuarterlyVigilanceSoReport />} />
              <Route path="ShortClosedCancelledOrderReport" element={<ShortClosedCancelledOrderReport />} />
              <Route path="MonthlyProcurementReport" element={<MonthlyProcurementReport />} />
              <Route path="IndentStatus" element={<IndentStatus />} />
            </Route>

            <Route path="/invReports" element={<InvReportsMain />}>
              <Route path="goodsIssue" element={<GoodsIssueReport />} />
              <Route path="igp" element={<IgpReport />} />
              <Route path="ogp" element={<OgpReport />} />
              <Route path="asset" element={<AssetReport />} />
              <Route path="stock" element={<StockReport />} />
            </Route>


            <Route path="/inventory">
              <Route path="gprn" element={<GPRN />} />
              <Route path="goodsInspection" element={<GoodsInspection />} />
              <Route path="goodsReturn" element={<Grv />} />
              <Route path="goodsReceipt" element={<Grn />} />
              <Route path="assetMaster" element={<Asset />} />
              <Route path="goodsIssue" element={<Isn />} />
              <Route path="goodsTransfer" element={<Form17 />} />
              <Route path="materialDisposal" element={<AssetDisposal />} />
              <Route path="ForDisposalAssets" element={<ForDisposalAssets />} />
              <Route path="outward" element={<Ogp />} />
              <Route path="inward" element={<Igp />} />
              <Route path="demandIssue" element={<Form20 />} />
            </Route>
          </Route>
        <Route path="/login" element={<Login />} />
        <Route path="/change-password" element={<ChangePassword />} />
        <Route path="*" element={<PageNotFound />} />
      </Routes>
    </BrowserRouter>
  );
};

export default RoutesComponent;*/
// Default: first role

const indentCreatorRoutes = (
  <>
    <Route path="/masters" element={<Master />} />
    <Route path="/procurement/indent/creation" element={<Indent1 />} />
    <Route path="/procurement/tender/evaluation" element={<TenderEvaluator />} />
    <Route path="/procurement/tender-evaluation" element={<TenderEvaluationPage />} />
    <Route path="/inventory/goodsInspection" element={<GoodsInspection />} />
    <Route path="/inventory/demandIssue" element={<Form20 />} />
    <Route path="/procurement/tender/Quotations" element={<Quotations />} />
    <Route path="/inventory/goodsTransfer" element={<Form17 />} />
    <Route path="/inventory/materialDisposal" element={<AssetDisposal />} />
    <Route path="/procurement/purchaseOrder" element={<PO />} />
    <Route path="/procurement/serviceOrder" element={<SO />} />
  </>
);

const storePurchaseRoutes = (
  <>

    <Route path="/masters" element={<Master />} />
    <Route path="/procurement/tender/request" element={<Tender />} />
    <Route path="/procurement/tender/evaluation" element={<TenderEvaluator />} />
    <Route path="/procurement/tender/gem" element={<TenderEvaluatorGem />} />
    <Route path="/procurement/tender/Quotations" element={<Quotations />} />
    <Route path="/procurement/tender-evaluation" element={<TenderEvaluationPage />} />
    <Route path="/inventory/goodsReceipt" element={<Grn />} />
    <Route path="/inventory/goodsInspection" element={<GoodsInspection />} />
    <Route path="/inventory/goodsReturn" element={<Grv />} />
    {/* <Route path="/inventory/goodsIssue" element={<Isn />} /> */}
    <Route path="/inventory/materialDisposal" element={<AssetDisposal />} />
    <Route path="/inventory/outward" element={<Ogp />} />
    <Route path="/inventory/inward" element={<Igp />} />
    <Route path="/inventory/goodsTransfer" element={<Form17 />} />
    <Route path="/inventory/ForDisposalAssets" element={<ForDisposalAssets />} />
    
  </>
);

const storePersonRoutes = (
  <>
    <Route path="/masters" element={<Master />} />
    <Route path="/inventory/gprn" element={<GPRN />} />
    <Route path="/inventory/goodsInspection" element={<GoodsInspection />} />
    <Route path="/inventory/goodsReturn" element={<Grv />} />
    <Route path="/inventory/goodsReceipt" element={<Grn />} />
    {/* <Route path="/inventory/goodsIssue" element={<Isn />} /> */}
    <Route path="/inventory/assetMaster" element={<Asset />} />
    <Route path="/inventory/goodsTransfer" element={<Form17 />} />
    <Route path="/inventory/materialDisposal" element={<AssetDisposal />} />
    <Route path="/inventory/demandIssue" element={<Form20 />} />
    <Route path="/inventory/ForDisposalAssets" element={<ForDisposalAssets />} />
    <Route path="/inventory/outward" element={<Ogp />} />
    <Route path="/inventory/inward" element={<Igp />} />
   

  </>
);


const purchasePersonnelRoutes = (
  <>

    <Route path="/masters" element={<Master />} />
    <Route path="/procurement/tender/request" element={<Tender />} />
    <Route path="/procurement/tender/evaluation" element={<TenderEvaluator />} />
    <Route path="/procurement/tender/gem" element={<TenderEvaluatorGem />} />
    <Route path="/procurement/tender/Quotations" element={<Quotations />} />
    <Route path="/procurement/cancellation/approval" element={<CancellationApprovalPage />} />
    <Route path="/procurement/tender-evaluation" element={<TenderEvaluationPage />} />
    <Route path="/procurement/gem-tender-evaluation" element={<GemTenderEvaluationPage />} />

      <Route path="/procurement/purchaseOrder" element={<PO />} />
      <Route path="/procurement/serviceOrder" element={<SO />} />

  </>
);
const committeeMemberRoutes = (
  <>
    <Route path="/procurement/tender/evaluation" element={<TenderEvaluator />} />
  </>
);

const directorRoutes = (
  <>
    <Route path="/procurement/tender/evaluation" element={<TenderEvaluator />} />
  </>
);
const tenderCreatorRoutes = (
  <>

    <Route path="/procurement/tender/request" element={<Tender />} />
    <Route path="/procurement/tender/evaluation" element={<TenderEvaluator />} />
    <Route path="/procurement/tender/gem" element={<TenderEvaluatorGem />} />
    <Route path="/procurement/tender/Quotations" element={<Quotations />} />

  </>
);

const poCreatorRoutes = (
  <>
   
    
      <Route path="/procurement/purchaseOrder" element={<PO />} />
    

  </>
);
const soCreatorRoutes = (
  <>
   
    
       <Route path="/procurement/serviceOrder" element={<SO />} />
    

  </>
);

// ✅ ADMIN PANEL ONLY ROUTES - Restricted routes for Admin role
const adminRoutes = (
  <>
    {/* Admin Panel Routes ONLY */}
    <Route path="/admin" element={<AdminDashboard />} />
    <Route path="/admin/lov" element={<ListOfValues />} />
    <Route path="/admin/approvers" element={<ApprovalWorkflow />} />
    <Route path="/admin/approval-limits" element={<ApprovalLimitsConfig />} />
    {/* <Route path="/admin/department-approvers" element={<DepartmentApproverMapping />} /> */}
    {/* <Route path="/admin/field-station-approvers" element={<FieldStationApproverConfig />} /> */}
    {/* <Route path="/admin/workflow-config" element={<FullWorkflowConfig />} /> */}
    <Route path="/admin/projects" element={<ProjectManagement />} />
    <Route path="/admin/budget" element={<BudgetManagement />} />
    <Route path="/admin/employee" element={<EmployeeRegistration />} />
    <Route path="/admin/user" element={<UserCreation />} />
     <Route path="/admin/committee" element={<CommitteeManagement />} />
  </>
);

// Now update the generateRoutes function
const generateRoutes = (roleName) => {
  console.log("🔹 generateRoutes called with:", roleName);

  switch (roleName) {
    case "Admin":  // ✅ ADD THIS CASE FIRST
      console.log("➡ using adminRoutes - FULL ACCESS");
      return adminRoutes;
    case "Indent Creator":
      console.log("➡ using indentCreatorRoutes");
      return indentCreatorRoutes;
    case "Store Purchase Officer":
      console.log("➡ using storePurchaseRoutes");
      return storePurchaseRoutes;
    case "Store Person":
      console.log("➡ using storePersonRoutes");
      return storePersonRoutes;
    case "Purchase personnel":
      console.log("➡ using purchasePersonnelRoutes");
      return purchasePersonnelRoutes;
    case "PO Creator":
      console.log("➡ using poCreatorRoutes");
      return poCreatorRoutes;
    case "SO Creator":
      console.log("➡ using soCreatorRoutes");
      return soCreatorRoutes;
    case "Tender Creator":
      console.log("➡ using tenderCreatorRoutes");
      return tenderCreatorRoutes;
      case "Committee Member":
      console.log("➡ using committeeMemberRoutes");
      return committeeMemberRoutes;
    case "Committee Chairman":
      console.log("➡ using committeeMemberRoutes (Chairman)");
      return committeeMemberRoutes;
    case "Director":
      console.log("➡ using directorRoutes");
      return directorRoutes;
    default:
      console.log("⚠ no routes for this role");
      return null;
  }
};

const RoutesComponent = () => {
  const auth = useSelector((state) => state.auth);
  console.log("🔹 RoutesComponent – auth:", auth);
  const roleName=auth.role;
  console.log("🔹 RoutesComponent – roleName:", roleName);

  // ✅ Redirect Admin users to Admin Dashboard instead of Main Dashboard
  const defaultRoute = roleName === "Admin" ? <AdminDashboard /> : <MainDashboard />;

  return (
    <BrowserRouter>
      <Routes>
        {/* Public Routes */}
        <Route path="/login" element={<Login />} />
        <Route path="/change-password" element={<ChangePassword />} />

        {/* Protected Routes - requires valid token */}
        <Route element={<ProtectedRoute />}>
          <Route path="/" element={<CustomLayout />}>
            <Route index element={defaultRoute} />
            {/* Queue - Only show for non-Admin roles */}
            {roleName !== "Admin" && <Route path="/queue" element={<QueueTable />} />}

            {/* Reports - Exclude Admin role */}
            {roleName !== "Admin" && (
              <Route path="/reports" element={<ReportsMain />}>
                <Route path="cpReport" element={<CpReport />} />
                <Route path="indentReport" element={<IndentReport />} />
                <Route path="technoMom" element={<TechnoMom />} />
                <Route path="vendorContract" element={<VendorContract />} />
                <Route path="procurementActivity" element={<ProcurementActivityReport />} />
                <Route path="PoList" element={<PoList />} />
                <Route path="SoList" element={<SoList />} />
                <Route path="PoStatus" element={<PoStatus />} />
                <Route path="SoStatus" element={<SoStatus />} />
                <Route path="IndentList" element={<IndentList />} />
                <Route path="QuarterlyVigilanceSoReport" element={<QuarterlyVigilanceSoReport />} />
                <Route path="ShortClosedCancelledOrderReport" element={<ShortClosedCancelledOrderReport />} />
                <Route path="MonthlyProcurementReport" element={<MonthlyProcurementReport />} />
                <Route path="IndentStatus" element={<IndentStatus />} />
              </Route>
            )}

            {/* Inventory Reports - Exclude Admin role */}
            {roleName !== "Admin" && (
              <Route path="/invReports" element={<InvReportsMain />}>
                {/* <Route path="goodsIssue" element={<GoodsIssueReport />} /> */}
                <Route path="igp" element={<IgpReport />} />
                <Route path="ogp" element={<OgpReport />} />
                <Route path="asset" element={<AssetReport />} />
                <Route path="stock" element={<StockReport />} />
              </Route>
            )}

            {/* Accounting Routes - Exclude Admin role */}
            {roleName !== "Admin" && (
              <>
                <Route path="/accounting/dashboard" element={<AccountingDashboard />} />
                <Route path="/accounting/vendor-ledger" element={<VendorLedger />} />
                <Route path="/accounting/trial-balance" element={<TrialBalance />} />
                <Route path="/accounting/payment-register" element={<PaymentRegister />} />
                <Route path="/accounting/tally-integration" element={<TallyIntegrationReport />} />
                <Route path="/procurement/contingencyPurchase" element={<ContingencyPurchase />} />
                <Route path="/procurement/paymentVoucher/Invoice" element={<Invoice />} />
                <Route path="/inventory/goodsTransfer" element={<Form17 />} />
              </>
            )}

            {/* Role-based routes */}
            {generateRoutes(roleName)}
          </Route>
        </Route>

        <Route path="*" element={<PageNotFound />} />
      </Routes>
    </BrowserRouter>
  );
};

export default RoutesComponent;
