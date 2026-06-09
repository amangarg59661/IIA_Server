import {
  LogoutOutlined,
  FileTextOutlined,
  FileAddOutlined,
  FileExclamationOutlined,
  MoneyCollectOutlined,
  GoldOutlined,
  CheckSquareOutlined,
  RollbackOutlined,
  ReconciliationOutlined,
  ApartmentOutlined,
  UsergroupAddOutlined,
  DashboardOutlined,
  BankOutlined,
  UserOutlined,
  CalendarOutlined,
  SafetyCertificateOutlined,
  TeamOutlined,
  EnvironmentOutlined,
  EyeOutlined,
} from "@ant-design/icons";
import { MdOutlineAddBox, MdOutlineSettings } from "react-icons/md";
import { BiTransferAlt } from "react-icons/bi";
import { TiFolderDelete } from "react-icons/ti";
import { CiPassport1 } from "react-icons/ci";
import { GoIssueReopened } from "react-icons/go";
export const commonMenu = [
  {
    key: "1",
    icon: <DashboardOutlined />,
    label: "Dashboard",
    path: "/",
  },
  {
    key: "2",
    icon: <UsergroupAddOutlined />,
    label: "Queue",
    path: "/queue",
  },
  {
    key: "3",
    icon: <FileTextOutlined />,
    label: "Reports",
    path: "/reports",
  },
  {
    key: "6",
    icon: <FileTextOutlined />,
    label: "Inventory Reports",
    path: "/invReports",
  },
   {
    key: "7",
    icon: <MoneyCollectOutlined />,
    label: "Contingency Purchase",
    path: "/procurement/contingencyPurchase",
  },
  {
    key: "9",
    icon: <MoneyCollectOutlined />,
    label: "Payment Voucher",
    path: "/procurement/PaymentVoucher/Invoice",
  },
   {
    key: "accounting",
    icon: <BankOutlined />,
    label: "Accounting",
    items: [
      // {
      //   key: "accounting-dashboard",
      //   icon: <DashboardOutlined />,
      //   label: "Accounting Dashboard",
      //   path: "/accounting/dashboard",
      // },
      {
        key: "vendor-ledger",
        icon: <UserOutlined />,
        label: "Vendor Ledgers",
        path: "/accounting/vendor-ledger",
      },
      {
        key: "tally-integration",
        icon: <FileTextOutlined />,
        label: "Tally Integration",
        path: "/accounting/tally-integration",
      },
      // {
      //   key: "trial-balance",
      //   icon: <CalculatorOutlined />,
      //   label: "Trial Balance",
      //   path: "/accounting/trial-balance",
      // },
      {
        key: "payment-register",
        icon: <CalendarOutlined />,
        label: "Payment Register",
        path: "/accounting/payment-register",
      },
       {
          key: "gd",
          icon: <BiTransferAlt />,
          label: "Goods Transfer",
          path: "/inventory/goodsTransfer",
        },
    ],
  },
];
// ✅ ADMIN PANEL ONLY - Restricted menu for Admin role
const adminMenuItems = [
  {
    key: "admin-panel",
    icon: <MdOutlineSettings />,
    label: "Admin Panel",
    items: [
      {
        key: "admin-dashboard",
        icon: <DashboardOutlined />,
        label: "Admin Dashboard",
        path: "/admin",
      },
      {
        key: "admin-lov",
        icon: <FileTextOutlined />,
        label: "List of Values",
        path: "/admin/lov",
      },
      {
        key: "admin-approvers",
        icon: <CheckSquareOutlined />,
        label: "Approval Workflow",
        path: "/admin/approvers",
      },
      {
        key: "admin-approval-limits",
        icon: <SafetyCertificateOutlined />,
        label: "Approval Limits",
        path: "/admin/approval-limits",
      },
      {
        key: "admin-dept-approvers",
        icon: <TeamOutlined />,
        label: "Department Approvers",
        path: "/admin/department-approvers",
      },
      {
        key: "admin-field-station",
        icon: <EnvironmentOutlined />,
        label: "Field Station In-Charges",
        path: "/admin/field-station-approvers",
      },
      {
        key: "admin-workflow-config",
        icon: <EyeOutlined />,
        label: "Full Workflow Config",
        path: "/admin/workflow-config",
      },
      {
        key: "admin-projects",
        icon: <ApartmentOutlined />,
        label: "Projects",
        path: "/admin/projects",
      },
      {
        key: "admin-budget",
        icon: <MoneyCollectOutlined />,
        label: "Budget",
        path: "/admin/budget",
      },
      {
        key: "admin-employee",
        icon: <UserOutlined />,
        label: "Employee Registration",
        path: "/admin/employee",
      },
      {
        key: "admin-user",
        icon: <UsergroupAddOutlined />,
        label: "User Creation",
        path: "/admin/user",
      },
      {
        key: "admin-committee",
        icon: <TeamOutlined />,
        label: "Committee Management",
        path: "/admin/committee",
      },
    ],
  },
];

export const sidebarMenus = {
  "Admin": adminMenuItems,  // ✅ ADD THIS - Admin gets everything
  "Indent Creator": [
    ...commonMenu,
    {
      key: "4",
      label: "Procurement",
      items: [
        {
          key: "4.1",
          label: "Indent",
          items: [
            {
              key: "4.1.1",
              icon: <FileAddOutlined />,
              label: "Indent Creation",
              path: "/procurement/indent/creation",
            },
          ],
        },
         {
            key: "4.2",
            label: "Tender",
            items:[
                 {
                    key: "4.2.1",
                    icon: <FileTextOutlined />,
                    label: "Tender Evaluation",
                    path: "/procurement/tender/evaluation",
                },
            ]
         },
          {
            key: "4.3",
            icon: <MoneyCollectOutlined />,
            label: "Purchase Order (PO)",
            path: "/procurement/purchaseOrder",
          },
          {
            key: "4.4",
            icon: <MoneyCollectOutlined />,
            label: "Service Order",
            path: "/procurement/serviceOrder",
          },
      ],
    },
    {
      key: "5",
      label: "Inventory",
      items: [
        {
          key: "5.1",
          icon: <CheckSquareOutlined />,
          label: "Goods Inspection",
          path: "/inventory/goodsInspection",
        },
        {
          key: "5.2",
          icon: <GoIssueReopened />,
          label: "Demand and Issue",
          path: "/inventory/demandIssue",
        },
         {
          key: "5.3",
          icon: <BiTransferAlt />,
          label: "Goods Transfer",
          path: "/inventory/goodsTransfer",
        },
        {
          key: "5.4",
          icon: <TiFolderDelete />,
          label: "Material Disposal",
          path: "/inventory/materialDisposal",
        },
      ],
    },
    {
      key: "8",
      icon: <MdOutlineSettings />,
      label: "Masters",
      path: "/masters",
    },
  ],

  "Store Purchase Officer": [
    ...commonMenu,
     {
      key: "4",
      label: "Procurement",
      items: [
        {
            key: "4.1",
            label: "Tender",
            items:[
                {
                    key: "4.2.1",
                    icon: <FileExclamationOutlined />,
                    label: "Tender Request",
                    path: "/procurement/tender/request",
                
                },
                 {
                    key: "4.2.2",
                    icon: <FileTextOutlined />,
                    label: "Tender Evaluation",
                    path: "/procurement/tender/evaluation",
                },
                 {
                    key: "4.2.3",
                    icon: <FileTextOutlined />,
                    label: "Gem Tender Evaluation",
                    path: "/procurement/tender/gem",
                },
            ]
        },
      ],
    },
    {
      key: "5",
      label: "Inventory",
      items: [
        {
          key: "5.1",
          icon: <CheckSquareOutlined />,
          label: "Goods Inspection",
          path: "/inventory/goodsInspection",
        },
        {
          key: "5.2",
          icon: <ReconciliationOutlined />,
          label: "Goods Receipt and Inspection",
          path: "/inventory/goodsReceipt",
        },
          {
            key: "5.3",
            icon: <RollbackOutlined />,
            label: "Goods Return",
            path: "/inventory/goodsReturn",
          },
          // {
          //   key: "5.4",
          //   icon: <MdOutlineAddBox />,
          //   label: "Goods Issue",
          //   path: "/inventory/goodsIssue",
          // },
        {
          key: "5.4",
          icon: <CiPassport1 />,
          label: "Outward Gate Pass",
          path: "/inventory/outward",
        },
        {
          key: "5.6",
          icon: <CiPassport1 />,
          label: "Inward Gate Pass",
          path: "/inventory/inward",
        },
        {
          key: "5.7",
          icon: <TiFolderDelete />,
          label: "Material Disposal",
          path: "/inventory/materialDisposal",
        },
        {
          key: "5.8",
          icon: <BiTransferAlt />,
          label: "Goods Transfer",
          path: "/inventory/goodsTransfer",
        },
      ],
    },
    {
    key: "8",
    icon: <MdOutlineSettings />,
    label: "Masters",
    path: "/masters",
  },
  ],

  "Store Person": [
    ...commonMenu,
    {
      key: "4",
      label: "Inventory",
      items: [
        {
          key: "4.1",
          icon: <GoldOutlined />,
          label: "GPRN",
          path: "/inventory/gprn",
        },
        {
          key: "4.2",
          icon: <CheckSquareOutlined />,
          label: "Goods Inspection",
          path: "/inventory/goodsInspection",
        },
          {
            key: "4.3",
            icon: <RollbackOutlined />,
            label: "Goods Return",
            path: "/inventory/goodsReturn",
          },
        {
          key: "4.4",
          icon: <ReconciliationOutlined />,
          label: "Goods Receipt and Inspection",
          path: "/inventory/goodsReceipt",
        },
          // {
          //   key: "4.5",
          //   icon: <MdOutlineAddBox />,
          //   label: "Goods Issue",
          //   path: "/inventory/goodsIssue",
          // },
           {
          key: "4.6",
          icon: <CiPassport1 />,
          label: "Outward Gate Pass",
          path: "/inventory/outward",
        },
        {
          key: "4.7",
          icon: <CiPassport1 />,
          label: "Inward Gate Pass",
          path: "/inventory/inward",
        },
        {
          key: "4.8",
          icon: <ApartmentOutlined />,
          label: "Asset Master",
          path: "/inventory/assetMaster",
        },
        {
          key: "4.9",
          icon: <BiTransferAlt />,
          label: "Goods Transfer",
          path: "/inventory/goodsTransfer",
        },
        {
          key: "4.1.1",
          icon: <TiFolderDelete />,
          label: "Material Disposal",
          path: "/inventory/materialDisposal",
        },
         {
          key: "4.1.2",
          icon: <GoIssueReopened />,
          label: "Demand and Issue",
          path: "/inventory/demandIssue",
        },
         {
            key: "4.1.3",
            icon: <TiFolderDelete />,
            label: "Asset Auction",
            path: "/inventory/ForDisposalAssets",
          },
      ],
    },
    {
      key: "5",
      icon: <MdOutlineSettings />,
      label: "Masters",
      path: "/masters",
    },
  ],
  "Purchase personnel" : [
     ...commonMenu,
     {
      key: "4",
      label: "Procurement",
      items: [
        {
            key: "4.1",
            label: "Tender",
            items:[
                {
                    key: "4.1.1",
                    icon: <FileExclamationOutlined />,
                    label: "Tender Request",
                    path: "/procurement/tender/request",
                },
                 {
                    key: "4.1.2",
                    icon: <FileTextOutlined />,
                    label: "Tender Evaluation",
                    path: "/procurement/tender/evaluation",
                },
                 {
                    key: "4.1.3",
                    icon: <FileTextOutlined />,
                    label: "Gem Tender Evaluation",
                    path: "/procurement/tender/gem",
                },
            ]
        },
          {
            key: "4.2",
            icon: <MoneyCollectOutlined />,
            label: "Purchase Order",
            path: "/procurement/purchaseOrder",
          },
           {
            key: "4.3",
            icon: <MoneyCollectOutlined />,
            label: "Service Order",
            path: "/procurement/serviceOrder",
          },
      ],
    },
     {
      key: "8",
      icon: <MdOutlineSettings />,
      label: "Masters",
      path: "/masters",
    },
  ],
   "PO Creator" : [
     ...commonMenu,
     {
      key: "4",
      label: "Procurement",
      items: [
          {
            key: "4.1",
            icon: <MoneyCollectOutlined />,
            label: "Purchase Order",
            path: "/procurement/purchaseOrder",
          },

      ],
    },
  ],
   "Tender Creator" : [
     ...commonMenu,
     {
      key: "4",
      label: "Procurement",
      items: [
        {
            key: "4.1",
            label: "Tender",
            items:[
                {
                    key: "4.1.1",
                    icon: <FileExclamationOutlined />,
                    label: "Tender Request",
                    path: "/procurement/tender/request",
                },
                 {
                    key: "4.1.2",
                    icon: <FileTextOutlined />,
                    label: "Tender Evaluation",
                    path: "/procurement/tender/evaluation",
                },
                 {
                    key: "4.1.3",
                    icon: <FileTextOutlined />,
                    label: "Gem Tender Evaluation",
                    path: "/procurement/tender/gem",
                },
            ]
        },

      ],
    },
  ],
   "SO Creator" : [
     ...commonMenu,
     {
      key: "4",
      label: "Procurement",
      items: [
           {
            key: "4.1",
            icon: <MoneyCollectOutlined />,
            label: "Service Order",
            path: "/procurement/serviceOrder",
          },
      ],
    },
  ],

  // Engineer In-Charge (renamed from Field Station In-Charge)
  "Engineer In-Charge": [
    ...commonMenu,
  ],

  // Professor In-Charge (NEW role with same responsibilities as Engineer In-Charge)
  "Professor In-Charge": [
    ...commonMenu,
  ],

  // Project Head role
  "Project Head": [
    ...commonMenu,
  ],

  // Purchase Head role
  "Purchase Head": [
    ...commonMenu,
  ],

  // Dean role
  "Dean": [
    ...commonMenu,
  ],

  // Head SEG role
  "Head SEG": [
    ...commonMenu,
  ],

  // Director role
  "Director": [
    ...commonMenu,
    {
    key: "4",
    label: "Procurement",
    items: [
      {
        key: "4.1",
        label: "Tender",
        items: [
          {
            key: "4.1.1",
            icon: <FileTextOutlined />,
            label: "Tender Evaluation",
            path: "/procurement/tender/evaluation",
          },
        ],
      },
    ],
  },
  ],

  // Reporting Officer role
  "Reporting Officer": [
    ...commonMenu,
  ],

  // Administrative Officer role
  "Administrative Officer": [
    ...commonMenu,
  ],

 
"Committee Member": [ ...commonMenu,
 
  {
    key: "4",
    label: "Procurement",
    items: [
      {
        key: "4.1",
        label: "Tender",
        items: [
          {
            key: "4.1.1",
            icon: <FileTextOutlined />,
            label: "Tender Evaluation",
            path: "/procurement/tender/evaluation",
          },
        ],
      },
    ],
  },
],

"Committee Chairman": [ ...commonMenu,
  
  {
    key: "4",
    label: "Procurement",
    items: [
      {
        key: "4.1",
        label: "Tender",
        items: [
          {
            key: "4.1.1",
            icon: <FileTextOutlined />,
            label: "Tender Evaluation",
            path: "/procurement/tender/evaluation",
          },
        ],
      },
    ],
  },
],
  default: [...commonMenu],
};