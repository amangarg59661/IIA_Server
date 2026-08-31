import React, { useState } from 'react'
import { FileTextOutlined, BarChartOutlined, PieChartOutlined, SolutionOutlined } from '@ant-design/icons';
import { useNavigate } from 'react-router-dom';
import CpReport from './CpReport';
import IndentReport from './IndentReport';
import TechnoMom from './TechnoMom';
import VendorContract from './VendorContractReport';
import ProcurementActivityReport from './ProcurementActivityReport';
import PoList from './PoList';
import SoList from './SoList';
import PoStatus from './PoStatus';
import SoStatus from './SoStatus';
import IndentList from './IndentList';
import QuarterlyVigilanceSoReport from './QuarterlyVigilanceSoReport';
import ShortClosedCancelledOrderReport from './ShortClosedCancelledOrderReport';
import MonthlyProcurementReport from './MonthlyProcurementReport';
import IndentStatus from './IndentStatus';
import PerformanceAndWarrantySecurity from './PerformanceAndWarrantySecurity';

const ReportsMain = () => {
    const tiles = [
        {
            id: 1,
            title: "Contingency Purchase Report",
            icon: <FileTextOutlined />,
            path:"/reports/contingencyPurchase"
        },
        {
            id: 2,
            title: "Indent Report",
            icon: <BarChartOutlined />,
            path:"/reports/indent"
        },
        {
            id: 3,
            title: "Techno MOM Report",
            icon: <PieChartOutlined />,
            path:"/reports/technoMom"
        },
        // {
        //     id: 4,
        //     title: "Vendor Contract Report",
        //     icon: <FileTextOutlined />,
        //     path: "/reports/vendorContract"
        // },
      /*  {
            id: 5,
            title: "Procurement Activity Report",
            icon: <SolutionOutlined />,
            path: "/reports/procurementActivity"
        },*/
        {
            id: 6,
            title: "PO List",
            icon: <SolutionOutlined />,
            path: "/reports/PoList"
        },
        {
            id: 7,
            title: "PO Status",
            icon: <BarChartOutlined />,
            path: "/reports/PoStatus"
        },
         {
            id: 8,
            title: "SO List",
            icon: <SolutionOutlined />,
            path: "/reports/SoList"
        },
         {
            id: 9,
            title: "SO Status",
            icon: <BarChartOutlined />,
            path: "/reports/SoStatus"
        },
         {
            id: 10,
            title: "Indent List",
            icon: <SolutionOutlined />,
            path: "/reports/IndentList"
        },
        {
            id:11,
            title:"Quarterly Vigilance Report",
             icon: <SolutionOutlined />,
            path: "/reports/QuarterlyVigilanceSoReport"
        },
        {
            id:12,
            title:"Short Closed Cancelled Order",
            icon: <SolutionOutlined />,
            path: "/reports/ShortClosedCancelledOrderReport"
        },
         {
            id:13,
            title:"Monthly Procurement Report",
            icon: <SolutionOutlined />,
            path: "/reports/MonthlyProcurementReport"
        },
         {
            id:14,
            title:"Indent Status",
            icon: <SolutionOutlined />,
            path: "/reports/IndentStatus"
        }, 
        {
            id:15,
            title:"Performance & Warranty Security",
            icon: <SolutionOutlined />,
            path: "/reports/PerformanceAndWarrantySecurity"
        },
      
    ]
    const [activeTab, setActiveTab] = useState(1)
    const renderReports = () => {
        switch(activeTab) {
            case 1:
                return <CpReport />
            case 2:
                return <IndentReport />
            case 3:
                return <TechnoMom />
            case 4:
                return <VendorContract/>
            case 5:
                return <ProcurementActivityReport />
            case 6:
                return <PoList />
            case 7:
                return <PoStatus />
            case 8:
                return <SoList />
            case 9:
                return <SoStatus />
            case 10:
                return <IndentList />
            case 11:
                return <QuarterlyVigilanceSoReport />
            case 12:
                return <ShortClosedCancelledOrderReport />
            case 13:
                return <MonthlyProcurementReport />
            case 14:
                return <IndentStatus/>
            case 15:
                return <PerformanceAndWarrantySecurity />
            default:
                return <h1>Contingency Purchase Report</h1>
        }
    }
  return (
    <div className='large-container'>
        <h1 className='!text-xl md:!text-xl font-semibold text-center'>Reports</h1>

        <div className="grid grid-cols-2 md:grid-cols-3 gap-4">
            {
                tiles.map(tile => (
                    <div key={tile.id} className={`flex gap-2 bg-gray-200 border-darkBlue rounded-md h-24 items-center p-4 cursor-pointer ${activeTab === tile.id ? "border-b-2 border-pink scale-105" : ""}`} onClick={() => setActiveTab(tile.id)}>
                        <div className="dashboard-tab-icon">
                        {tile.icon}
                        </div>
                        <div className="flex-1 text-right !text-md font-semibold">
                        {tile.title}
                        </div>
                        </div>
                ))
            }
        </div>

        {renderReports()}
    </div>
  )
}

export default ReportsMain
