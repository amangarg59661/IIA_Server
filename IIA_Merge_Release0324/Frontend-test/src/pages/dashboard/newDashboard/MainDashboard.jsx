/*import React, { useState, useEffect, useMemo } from "react";
import TableComponent from "../../../components/DKG_Table";
import { FileTextOutlined, FileDoneOutlined, FileSearchOutlined } from '@ant-design/icons';

const MainDashboard = () => {
  const [activeTab, setActiveTab] = useState(1);
  const [tableColumns, setTableColumns] = useState([]);
  const [tableData, setTableData] = useState([]);
  const [tileData, setTileData] = useState([]);

  const tileColorList = [
    "#004B4D", // Deep Teal
    "#2E1A47", // Midnight Purple
    "#2B3A70", // Slate Blue
    "#3B3C36", // Dark Olive Green
    "#4A0C0C", // Crimson Red
    "#1E1A78", // Indigo Night
    "#003B5C", // Deep Sea Blue
    "#4A5A3D"  // Moss Green
  ];

  // Define your tile data locally
  const dashboardTiles = [
    {
      id: 1,
      title: "Report 1",
      icon: <FileTextOutlined />, // You can use an emoji, icon component, or image.
      color: tileColorList[0],
      // Define table columns for tile 1 (without filters)
      tableColumns: [
        { title: "Order Id", dataIndex: "col1", key: "col1" },
        { title: "Mode of Procurement", dataIndex: "col2", key: "col2" },
        { title: "Under AMC", dataIndex: "col3", key: "col3" },
        { title: "AMC expiry date", dataIndex: "col4", key: "col4" },
        { title: "AMC for", dataIndex: "col5", key: "col5" },
        { title: "End User", dataIndex: "col6", key: "col6" },
        { title: "No. of Participants", dataIndex: "col7", key: "col7" },
        { title: "Value", dataIndex: "col8", key: "col8" },
        { title: "Location", dataIndex: "col9", key: "col9"},
        { title: "Vendor Name", dataIndex: "col10", key: "col10"},
        { title: "Previously renewed AMCs", dataIndex: "col11", key: "col11"},
        { title: "Category Security", dataIndex: "col12", key: "col12"},
        { title: "Validity of Security", dataIndex: "col13", key: "col13"},
      ],
      // Define table data for tile 1
      data: [
        {
          id: 1,
            col1: "111",
            col2: "112",
            col3: "113",
            col4: "114",
            col5: "115",
            col6: "116",
            col7: "117",
            col8: "118",
            col9: "119",
            col10: "1100",
            col11: "1111",
            col12: "1112",
            col13: "1113",
        },
        {
          id: 2,
            col1: "121",
            col2: "122",
            col3: "123",
            col4: "124",
            col5: "125",
            col6: "126",
            col7: "127",
            col8: "128",
            col9: "129",
            col10: "1210",
            col11: "1211",
            col12: "1212",
            col13: "1213",
        },
        {
          id: 3,
            col1: "131",
            col2: "132",
            col3: "133",
            col4: "134",
            col5: "135",
            col6: "136",
            col7: "137",
            col8: "138",
            col9: "139",
            col10: "1310",
            col11: "1311",
            col12: "1312",
            col13: "1313",
        },
      ],
    },
    {
      id: 2,
      title: "Report 2",
      icon: <FileDoneOutlined />,
      color: tileColorList[1],
      tableColumns: [
        { title: "Order Id", dataIndex: "col1", key: "col1" },
        { title: "Mode of Procurement", dataIndex: "col2", key: "col2" },
        { title: "Under AMC", dataIndex: "col3", key: "col3" },
        { title: "AMC expiry date", dataIndex: "col4", key: "col4" },
        { title: "AMC for", dataIndex: "col5", key: "col5" },
        { title: "End User", dataIndex: "col6", key: "col6" },
        { title: "No. of Participants", dataIndex: "col7", key: "col7" },
        { title: "Value", dataIndex: "col8", key: "col8" },
        { title: "Location", dataIndex: "col9", key: "col9"},
        { title: "Vendor Name", dataIndex: "col10", key: "col10"},
        { title: "Previously renewed AMCs", dataIndex: "col11", key: "col11"},
        { title: "Category Security", dataIndex: "col12", key: "col12"},
        { title: "Validity of Security", dataIndex: "col13", key: "col13"},
      ],
      data: [
        {
            id: 1,
            col1: "211",
            col2: "212",
            col3: "213",
            col4: "214",
            col5: "215",
            col6: "216",
            col7: "217",
            col8: "218",
            col9: "219",
            col10: "2100",
            col11: "2111",
            col12: "2112",
            col13: "2113",
          },
          {
            id: 2,
            col1: "221",
            col2: "222",
            col3: "223",
            col4: "224",
            col5: "225",
            col6: "226",
            col7: "227",
            col8: "228",
            col9: "229",
            col10: "2210",
            col11: "2211",
            col12: "2212",
            col13: "2213",
          },
          {
            id: 3,
            col1: "231",
            col2: "232",
            col3: "233",
            col4: "234",
            col5: "235",
            col6: "236",
            col7: "237",
            col8: "238",
            col9: "239",
            col10: "2310",
            col11: "2311",
            col12: "2312",
            col13: "2313",
          },
      ],
    },
    {
      id: 3,
      title: "Report 3",
      icon: <FileSearchOutlined />,
      color: tileColorList[2],
      tableColumns: [
        { title: "Order Id", dataIndex: "col1", key: "col1" },
        { title: "Mode of Procurement", dataIndex: "col2", key: "col2" },
        { title: "Under AMC", dataIndex: "col3", key: "col3" },
        { title: "AMC expiry date", dataIndex: "col4", key: "col4" },
        { title: "AMC for", dataIndex: "col5", key: "col5" },
        { title: "End User", dataIndex: "col6", key: "col6" },
        { title: "No. of Participants", dataIndex: "col7", key: "col7" },
        { title: "Value", dataIndex: "col8", key: "col8" },
        { title: "Location", dataIndex: "col9", key: "col9"},
        { title: "Vendor Name", dataIndex: "col10", key: "col10"},
        { title: "Previously renewed AMCs", dataIndex: "col11", key: "col11"},
        { title: "Category Security", dataIndex: "col12", key: "col12"},
        { title: "Validity of Security", dataIndex: "col13", key: "col13"},
      ],
      data: [
        {
            id: 1,
            col1: "311",
            col2: "312",
            col3: "313",
            col4: "314",
            col5: "315",
            col6: "316",
            col7: "317",
            col8: "318",
            col9: "319",
            col10: "3100",
            col11: "3111",
            col12: "3112",
            col13: "3113",
          },
          {
            id: 2,
            col1: "321",
            col2: "322",
            col3: "323",
            col4: "324",
            col5: "325",
            col6: "326",
            col7: "327",
            col8: "328",
            col9: "329",
            col10: "3210",
            col11: "3211",
            col12: "3212",
            col13: "3213",
          },
          {
            id: 3,
            col1: "331",
            col2: "332",
            col3: "333",
            col4: "334",
            col5: "335",
            col6: "336",
            col7: "337",
            col8: "338",
            col9: "339",
            col10: "3310",
            col11: "3311",
            col12: "3312",
            col13: "3313",
          },
      ],
    },
    // Add more tiles as needed...
  ];

  // On component mount, set the tile data and default table data/columns
  useEffect(() => {
    setTileData(dashboardTiles);
    if (dashboardTiles.length > 0) {
      setTableColumns(dashboardTiles[0].tableColumns);
      setTableData(dashboardTiles[0].data);
    }
  }, []);

  // When a tile is clicked, update the table columns and data
  const handleTileClick = (id) => {
    const tile = tileData.find((item) => item.id === id);
    if (tile) {
      setTableColumns(tile.tableColumns);
      setTableData(tile.data);
      setActiveTab(id);
    }
  };

  // Generate table columns with filters for the current tableData.
  // This example creates a filter list for each column based on unique values found in tableData.
  const filteredColumns = useMemo(() => {
    return tableColumns.map((column) => {
      const uniqueValues = Array.from(
        new Set(tableData.map((row) => row[column.dataIndex]))
      );
      return {
        ...column,
        // Define filter options for this column
        filters: uniqueValues.map((val) => ({ text: val, value: val })),
        // Basic filtering: check if the column's value contains the filter value.
        onFilter: (value, record) =>
          record[column.dataIndex]
            .toString()
            .toLowerCase()
            .includes(value.toLowerCase()),
      };
    });
  }, [tableData, tableColumns]);

  // Render the dashboard tiles
  const renderTiles = () =>
    tileData.map((item) => (
      <div
        key={item.id}
        onClick={() => handleTileClick(item.id)}
        className={`cursor-pointer p-2 rounded-md grid grid-cols-3 h-32 gap-8 ${
          activeTab === item.id ? "border-b-2 border-pink" : ""
        }`}
        style={{ backgroundColor: item.color }}
      >
        <span className="dashboard-tab-icon text-white">{item.icon}</span>
        <div className="flex flex-col items-center justify-center gap-1 col-span-2">
          {/* You can replace this hard-coded number with a dynamic value if needed */
         /* <h3 className="font-semibold !text-2xl text-white text-left w-full">
            54
          </h3>
          <div className="w-full text-white text-left">{item.title}</div>
        </div>
      </div>
    ));

  return (
    <div className="px-4 flex flex-col gap-6">
      <h1 className="font-semibold !text-3xl text-center">Dashboard</h1>
      <section className="grid grid-cols-2 md:grid-cols-3 gap-4 md:gap-6">
        {renderTiles()}
      </section>
      <section>
        {/* Pass the filteredColumns to your TableComponent */
      /*  <TableComponent dataSource={tableData} columns={filteredColumns} />
      </section>
    </div>
  );
};

export default MainDashboard;*/
import React, { useState ,useEffect} from "react";
import { FileTextOutlined, BarChartOutlined, SolutionOutlined,PieChartOutlined  } from '@ant-design/icons';
import CpReport from '../../reports/CpReport';
import IndentReport from '../../reports/IndentReport';
import PoList from '../../reports/PoList';
import SoList from '../../reports/SoList';
import { BarChart, Bar, XAxis, YAxis, Tooltip, CartesianGrid, ResponsiveContainer, PieChart, Pie, Cell, Legend } from "recharts";
import PoStatus from "../../reports/PoStatus";
import TechnoMom from "../../reports/TechnoMom";
import VendorContract from "../../reports/VendorContractReport";
import SoStatus from "../../reports/SoStatus";
import IndentList from "../../reports/IndentList";
import QuarterlyVigilanceSoReport from "../../reports/QuarterlyVigilanceSoReport";
import ShortClosedCancelledOrderReport from "../../reports/ShortClosedCancelledOrderReport";
import MonthlyProcurementReport from "../../reports/MonthlyProcurementReport";
import PerformanceAndWarrantySecurity from "../../reports/PerformanceAndWarrantySecurity";
import IndentStatus from "../../reports/IndentStatus";
import { useSelector } from "react-redux";  
import PendingRecordsReport from "../../reports/pendingRecords";
import axios from "axios";
import AssetReport from "../../reports/AssetReport";
import StockReport from "../../reports/StockReport";
import GoodsIssueReport from "../../reports/GoodsIssueReport";
import IgpReport from "../../reports/IgpReport";
import OgpReport from "../../reports/OgpReport";
import RejectedGiReport from "../../reports/RejectedGiReport";
import IgpMaterialInReport from "../../reports/IgpMaterialInReport";
import WithInFieldStationGtReport from "../../reports/withInFieldStationGtReport";
import DemandAndIssueReport from "../../reports/DemandAndIssueReport";
import AssetDisposalReport from "../../reports/ApprovedAssetDisposalReport";
import DisposalReport from "../../reports/DisposalReport";

const MainDashboard = () => {
  const [activeTab, setActiveTab] = useState(1);
  const [chartDataMap, setChartDataMap] = useState({}); // Store chart data per report
  const COLORS = ["#0088FE", "#00C49F", "#FFBB28", "#FF8042"];
  const [selectedBarKey, setSelectedBarKey] = useState("vendorName"); // NEW: default bar attribute
  const [selectedPieKey, setSelectedPieKey] = useState("project");
  const auth = useSelector((state) => state.auth);
  const [pendingCount, setPendingCount] = useState(0);

  const roleName = auth.role; 
  const userId = auth.userId;

/*
  useEffect(() => {
  if (roleName) {
     fetch(`http://localhost:8081/astro-service/allPendingRecords?roleName=${encodeURIComponent(roleName)}`) 
      .then(res => res.json())
      .then(data => setPendingCount(data.responseData.length))
      .catch(err => console.error(err));
  }
}, [roleName]);*/
useEffect(() => {
  if (roleName && userId) {
    axios
      .get("/allPendingRecords", {
        params: {
          roleName: roleName
        }
      })
      .then((res) => setPendingCount(res.data.responseData.length))
      .catch((err) => console.error(err));
  }
}, [roleName, userId]);



  // Callback to update chart data dynamically
const handleChartData = (id, barData, pieData) => {
  setChartDataMap(prev => ({
    ...prev,
    [id]: { chart1: barData, chart2: pieData }
  }));
};

  // Only store component class/functions, not JSX
  const tiles = [
      { id: 1, title: `Pending Total Records: ${pendingCount}`, icon: <BarChartOutlined />, component: PendingRecordsReport, attributes:["status"],  roles:[roleName]},
    { id: 19, title: "Contingency Purchase", icon: <FileTextOutlined />, component: CpReport, attributes: [ 
  "contigencyId",
  "vendorName",
  "projectName",
  "paymentToVendor",
  "paymentToEmployee",
  "purpose",
  "createdBy",
  "pendingWith",
  "pendingFrom",
  "status",
  "action",], roles: [
      "CP Creator",
      "Store Personnel",
      "Reporting Officer",
      "Project Head",
      "Billing Section Personnel",
      "Accounts Officer",
      "Administrative Officer",
      "Store Purchase Officer", "Purchase personnel",
    ] },
    { id: 2, title: "Indent", icon: <BarChartOutlined />, component: IndentReport ,attributes:[
    "indentId",
    "approvedDate",
    "assignedTo",
    "tenderRequest",
    "modeOfTendering",
    "correspondingPoSo",
    "statusOfPoSo",
    "submittedDate",
    "pendingApprovalWith",
    "poSoApprovedDate",
    "material",
    "materialCategory",
    "materialSubCategory",
    "vendorName",
    "indentorName",
    "valueOfIndent",
    "valueOfPo",
    "project",
    "grinNo",
    "invoiceNo",
    "gissNo",
    "valuePendingToBePaid",
    "currentStageOfIndent",
    "shortClosedAndCancelled",
    "reasonForShortClosure",
    "gemContractFileName"
  ], roles:[roleName]},
    {
  id: 3,
  title: "Techno MOM",
  icon: <PieChartOutlined />,
  component: TechnoMom, // your component
  attributes: [
    "date",
    "uploadedTechnoCommercialMoMReports",
    "poWoNumber",
    "value",
  ], roles:["Store Purchase Officer", "Purchase personnel"],
},
    { id: 6, title: "PO List", icon: <SolutionOutlined />, component: PoList, attributes: [ "approvedDate",
  "poId",
  "vendorName",
  "value",
  "tenderId",
  "project",
  "vendorId",
  "indentIds",
  "modeOfProcurement"], roles:["PO Creator","Store Purchase Officer", "Purchase personnel", "Administrative Officer", "Account Officer", "Project Head", "Director", "Dean", "Head SEG", "Store Person", "Indent Creator", "Reporting Officer", "Engineer In-Charge", "Professor In-Charge", "Computer Committee Chairman", "Purchase Head"] },
    { id: 7, title: "PO Status", icon: <SolutionOutlined />, component: PoStatus, attributes: [  "poId",
    "tenderId",
    "indentIds",
    "vendorName",
    "value",
    "submittedDate",
    "pendingWith",
    "pendingFrom",
    "status",
    "asOnDate",
], roles:["PO Creator", "Store Purchase Officer", "Purchase personnel", "Administrative Officer", "Account Officer", "Project Head", "Director", "Dean", "Head SEG", "Store Person", "Indent Creator", "Reporting Officer", "Engineer In-Charge", "Professor In-Charge", "Computer Committee Chairman", "Purchase Head"] },
{
  id: 4,
  title: "Vendor Contract",
  icon: <FileTextOutlined />,
  component: VendorContract,  
  attributes: [
    "orderId",
    "modeOfProcurement",
    "underAmc",
    "amcExpiryDate",
    "amcFor",
    "endUser",
    "noOfParticipants",
    "value",
    "location",
    "vendorName",
    "previouslyRenewedAmcs",
    "categoryOfSecurity",
    "validityOfSecurity"
  ], roles:["Store Purchase Officer", "Purchase personnel","Store Person"]
},

    { id: 8, title: "SO List", icon: <SolutionOutlined />, component: SoList, attributes:["approvedDate",
  "soId",
  "vendorName",
  "value",
  "tenderId",
  "project",
  "vendorId",
  "indentIds",
  "modeOfProcurement"], roles:["SO Creator", "Store Purchase Officer", "Purchase personnel", "Administrative Officer", "Account Officer", "Project Head", "Director", "Dean", "Head SEG", "Store Person", "Indent Creator", "Reporting Officer", "Engineer In-Charge", "Professor In-Charge", "Computer Committee Chairman", "Purchase Head"]
},
    {
  id: 9,
  title: "SO Status",
  icon: <BarChartOutlined />,
  component: SoStatus,
  attributes: [
    "soId",
    "tenderId",
    "indentIds",
    "vendorName",
    "value",
    "submittedDate",
    "pendingWith",
    "pendingFrom",
    "status",
    "asOnDate"
  ], roles:["SO Creator", "Indent Creator", "Store Purchase Officer", "Purchase personnel", "Administrative Officer", "Account Officer", "Project Head", "Director", "Dean", "Head SEG", "Store Person", "Reporting Officer", "Engineer In-Charge", "Professor In-Charge", "Computer Committee Chairman", "Purchase Head"]
},{
  id: 10,
  title: "Indent List",
  icon: <SolutionOutlined />,
  component: IndentList,
  attributes: [
    "indentId",
    "indentorName",
    "indentorMobileNo",
    "indentorEmailAddress",
    "consignesLocation",
    "projectName",
    "submittedDate",
    "pendingWith",
    "pendingFrom",
    "status",
    "asOnDate",
    "createdBy",
  ], roles:["Indent Creator", "Reporting Officer", "Administrative Officer", "Engineer In-Charge", "Professor In-Charge", "Computer Committee Chairman", "Dean", "Head SEG", "Director", "Project Head", "Purchase Head", "Store Purchase Officer", "Purchase personnel", "Store Person"]
},{
  id: 11,
  title: "Quarterly Vigilance",
  icon: <SolutionOutlined />,
  component: QuarterlyVigilanceSoReport,
  attributes: [
    "orderNo",
    "orderDate",
    "value",
    "vendorName",
    "location",
    "deliveryDate"
  ] , roles:["Store Purchase Officer", "Purchase personnel","Store Person"]
},{
  id: 12,
  title: "Short Closed Cancelled Order",
  icon: <SolutionOutlined />,
  component: ShortClosedCancelledOrderReport,
  attributes: [
    "poId",
    "tenderId",
    "indentIds",
    "value",
    "vendorName",
    "submittedDate",
    "reason",
    "materials"
  ],roles:["Store Purchase Officer", "Purchase personnel","Store Person"]
},{
  id: 13,
  title: "Monthly Procurement Report",
  icon: <SolutionOutlined />,
  component: MonthlyProcurementReport,
  attributes: [
    "month",
    "poNumber",
    "modeOfProcurement",
    "date",
    "indentIds",
    "value",
    "vendorName"
  ],roles:["Store Purchase Officer", "Purchase personnel","Store Person"]
},{
    id: 15,
    title: "Performance & Warranty Security",
    icon: <SolutionOutlined />,
    component: PerformanceAndWarrantySecurity,
    attributes: [
        "poId",
        "createdDate",
        "modeOfProcurement",
        "vendorName",
        "titleOfTender",
        "totalValueOfPo",
        "typeOfSecurity",
        "securityNumber",
        "securityDate",
        "expiryDate",
        "securityAmount"
    ],roles:["Store Purchase Officer", "Purchase personnel","Store Person"]
},{
  id: 16,
  title: "Indent Status",
  icon: <SolutionOutlined />,
  component: IndentStatus,
  attributes: [
    "requestId",
    "createdBy",
    "modifiedBy",
    "status",
    "nextAction",
    "action",
    "currentRole",
    "nextRole",
    "remarks",
    "modificationDate",
    "createdDate"
  ],roles:["Indent Creator", "Reporting Officer", "Administrative Officer", "Engineer In-Charge", "Professor In-Charge", "Computer Committee Chairman", "Dean", "Head SEG", "Director", "Project Head", "Purchase Head", "Store Purchase Officer", "Purchase personnel", "Store Person"]
},{
  id: 17,
  title: "Asset",
  icon: <SolutionOutlined />,
  component: AssetReport,
  attributes: [
    "Asset ID",
    "materialCode",
    "materialDesc",
    "assetDesc",
    "makeNo",
    "serialNo",
    "modelNo",
    "uomId",
    "poId",
    "poValue",
    "vendorId"
  ],roles:[roleName]
},
{
  id: 18,
  title: "Stock",
  icon: <SolutionOutlined />,
  component: StockReport,
  attributes: [
    "assetId",
    "assetDesc",
    "materialCode",
    "materialDesc",
    "uomId",
    "totalQuantity",
    "bookValue",
    "depriciationRate",
    "unitPrice",
  ],
  roles: [roleName]
},
{
  id: 20,
  title: "GoodsIssue",
  icon: <SolutionOutlined />,
  component: GoodsIssueReport,
  attributes: [
    "issueNoteId",
    "issueNoteType",
    "issueDate",
    "consigneeDetail",
    "indentorName",
    "fieldStation",
    "locationId",
  ],
  roles: [roleName]
},
{
  id: 21,
  title: "IGP Report",
  icon: <SolutionOutlined />,
  component: IgpReport,
  attributes: [
    "igpProcessId",
    "ogpSubProcessId",
    "igpDate",
    "locationId",
    "createdBy"
  ],
  roles: [roleName]
},
{
  id: 22,
  title: "OGP Report",
  icon: <SolutionOutlined />,
  component: OgpReport,
  attributes: [
    "ogpProcessId",
    "issueNoteId",
    "ogpDate",
    "locationId",
    "createdBy"
  ],
  roles: [roleName]
},
{
  id: 23,
  title: "Rejected GI Report",
  icon: <SolutionOutlined />,
  component: RejectedGiReport,
  attributes: [
    "ogpSubProcessId",
    "giId",
    "ogpType",
    "status",
    "locationId",
    "createdBy",
    "senderName",
    "receiverName",
    "receiverLocation",
    "ogpDate",
    "returnDate"
  ],
  roles: [roleName]
},
{
  id: 24,
  title: "IGP Material In",
  icon: <SolutionOutlined />,
  component: IgpMaterialInReport,
  attributes: [
    "id",
    "igpType",
    "status",
    "locationId",
    "createdBy",
    "indentId",
    "igpDate"
  ],
  roles: [roleName]
},
{
  id: 25,
  title: "Goods Transfer",
  icon: <SolutionOutlined />,
  component: WithInFieldStationGtReport,
  attributes: [
    "gtId",
    "type",
    "senderLocationId",
    "receiverLocationId",
    "senderCustodianId",
    "receiverCustodianId",
    "status",
    "gtDate",
    "createDate",
    "createdBy"
  ],
  roles: [roleName]
},
{
  id: 26,
  title: "Demand And Issue",
  icon: <SolutionOutlined />,
  component: DemandAndIssueReport,
  attributes: [
    "id",
    "senderLocationId",
    "status",
    "senderCustodianId",
    "issueDate",
    "issueBy",
  ],
  roles: [roleName]
},
{
  id: 27,
  title: "Asset Disposal",
  icon: <SolutionOutlined />,
  component: AssetDisposalReport,
  attributes: [
    "disposalId",
    "disposalDate",
    "locationId",
    "custodianId",
    "status",
    "action"
  ],
  roles: [roleName]
},
{
  id: 28,
  title: "Auction Asset Disposal",
  icon: <SolutionOutlined />,
  component: DisposalReport,
  attributes: [
    "auctionId",
    "auctionCode",
    "auctionDate",
    "reservePrice",
    "auctionPrice",
    "vendorName"
  ],
  roles: [roleName]
}




  ];

  const activeTile = tiles.find(tile => tile.id === activeTab);
//  const activeChartData = chartDataMap[activeTab] || []; // Get chart data for active tab
const activeChartData = chartDataMap[activeTab] || { chart1: [], chart2: [] };

const visibleTiles = tiles.filter(tile =>
  tile.roles?.includes(roleName) // only show tiles that allow the user role
);
useEffect(() => {
  if (activeTile) {
    // Set defaults only if nothing is selected yet
    setSelectedBarKey(prev => prev || activeTile.attributes[0] || "status");
    setSelectedPieKey(prev => prev || activeTile.attributes[0] || "status");
  }
}, [activeTile]);



  return (
    <div className="px-4 flex flex-col gap-6">
      <h1 className="font-semibold !text-3xl text-center">Dashboard</h1>

      {/* Tiles */}
      <div className="grid grid-cols-2 md:grid-cols-3 gap-4">
        {visibleTiles.map(tile => (
          <div
            key={tile.id}
            className={`flex gap-2 bg-gray-200 border-darkBlue rounded-md h-24 items-center p-4 cursor-pointer ${
              activeTab === tile.id ? "border-b-2 border-pink scale-105" : ""
            }`}
            onClick={() => setActiveTab(tile.id)}
          >
            <div className="dashboard-tab-icon">{tile.icon}</div>
            <div className="flex-1 text-right !text-md font-semibold">{tile.title}</div>
          </div>
        ))}
      
      </div>
      {activeTile && (
  <div className="flex gap-4 mt-4 items-center">
    <div>
      <label>Bar Chart Attribute: </label>
      <select value={selectedBarKey} onChange={e => setSelectedBarKey(e.target.value)}>
        {activeTile.attributes.map(attr => (
          <option key={attr} value={attr}>{attr}</option>
        ))}
      </select>
    </div>
    <div>
      <label>Pie Chart Attribute: </label>
      <select value={selectedPieKey} onChange={e => setSelectedPieKey(e.target.value)}>
        {activeTile.attributes.map(attr => (
          <option key={attr} value={attr}>{attr}</option>
        ))}
      </select>
    </div>
  </div>
)}


      {/* Bar Chart */}
    
{activeTile && (chartDataMap[activeTab]?.chart1?.length > 0 || chartDataMap[activeTab]?.chart2?.length > 0) && (
  <div className="grid md:grid-cols-2 gap-6 mt-6">

    {/* Bar Chart */}
    <div style={{ height: 300 }}>
      <ResponsiveContainer width="100%" height="100%">
        <BarChart data={chartDataMap[activeTab]?.chart1 || []}>
          <CartesianGrid strokeDasharray="3 3" />
          <XAxis dataKey="name" />
          <YAxis domain={[0, 'auto']} />
          <Tooltip />
          <Bar dataKey="value" fill="#8884d8" />
        </BarChart>
      </ResponsiveContainer>
    </div>

    {/* Pie Chart */}
    <div style={{ height: 300 }}>
      <ResponsiveContainer width="100%" height="100%">
        <PieChart>
          <Pie
            data={chartDataMap[activeTab]?.chart2 || []}  // safe check
            dataKey="value"
            nameKey="name"
            outerRadius={100}
            fill="#82ca9d"
            label
          >
            {(chartDataMap[activeTab]?.chart2 || []).map((entry, index) => (
              <Cell key={`cell-${index}`} fill={COLORS[index % COLORS.length]} />
            ))}
          </Pie>
          <Tooltip />
          <Legend />
        </PieChart>
      </ResponsiveContainer>
    </div>

  </div>
)}

      {/* Report Table Component */}
      <div className="mt-6">
        {activeTile  && React.createElement(activeTile.component, {selectedBarKey,
          selectedPieKey,
          roleName,
         onChartData: (barData, pieData) => handleChartData(activeTile.id, barData, pieData)

        })}
      </div>
    </div>
  );
};

export default MainDashboard;


