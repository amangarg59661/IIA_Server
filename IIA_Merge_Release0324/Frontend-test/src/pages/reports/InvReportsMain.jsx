import React, { useState } from 'react'
import { DatabaseOutlined, BoxPlotOutlined, ImportOutlined, ExportOutlined, InboxOutlined } from '@ant-design/icons';
import AssetReport from './AssetReport';
import StockReport from './StockReport';
import GoodsIssueReport from './GoodsIssueReport';
import IgpReport from './IgpReport';
import OgpReport from './OgpReport';
import RejectedGiReport from './RejectedGiReport';
import IgpMaterialInReport from './IgpMaterialInReport';
import WithInFieldStationGtReport from './withInFieldStationGtReport';
import DemandAndIssueQueue from '../dashboard/queue/DemandAndIssueQueue';
import DemandAndIssueReport from './DemandAndIssueReport';
import AssetDisposalReport from './ApprovedAssetDisposalReport';
import DisposalReport from './DisposalReport';

const InvReportsMain = () => {
    const tiles = [
        {
            id: 1,
            title: "Asset Report",
            icon: <DatabaseOutlined />,
            path:"/reports/asset"
        },
        {
            id: 2,
            title: "Stock Report",
            icon: <BoxPlotOutlined />,
            path:"/reports/stock"
        },
        // {
        //     id: 3,
        //     title: "Goods Issue Report",
        //     icon: <ExportOutlined />,
        //     path:"/reports/goodsIssue"
        // },
        {
            id: 4,
            title: "IGP Report",
            icon: <ImportOutlined />,
            path: "/reports/igp"
        },
        {
            id: 5,
            title: "OGP Report",
            icon: <InboxOutlined />,
            path: "/reports/ogp"
        },
        {
            id: 6,
            title: "OGP Rejected Gi Report",
            icon: <InboxOutlined />,
            path: "/reports/RejectedGiReport"
        },
        {
            id: 7,
            title: "IGP Material In Report",
            icon: <InboxOutlined />,
            path: "/reports/IgpMaterialInReport"
        },
         {
            id: 8,
            title: "Gt Report",
            icon: <InboxOutlined />,
            path: "/reports/WithINFieldStationGtReport"
        }, {
            id: 9,
            title: "Deamnd And Issue Report",
            icon: <InboxOutlined />,
            path: "/reports/DemandAndIssueReport"
        },
         {
            id: 10,
            title: "Approved Assets Disposal Report",
            icon: <InboxOutlined />,
            path: "/reports/AssetDisposalReport"
        },
        {
            id: 11,
            title: "Disposal Report",
            icon: <InboxOutlined />,
            path: "/reports/DisposalReport"
        },
        
    ]
    const [activeTab, setActiveTab] = useState(1)

    const renderReports = () => {
        switch(activeTab) {
            case 1:
                return <AssetReport />
            case 2:
                return <StockReport />
            case 3:
                return <GoodsIssueReport />
            case 4:
                return <IgpReport />
            case 5:
                return <OgpReport />
            case 6:
                return <RejectedGiReport />
            case 7:
                return <IgpMaterialInReport />
            case 8:
                return <WithInFieldStationGtReport />
             case 9:
                return <DemandAndIssueReport />
            case 10:
                return <AssetDisposalReport />
            case 11:
                return <DisposalReport />
            default:
                return <h1>Asset Report</h1>
        }
    }

    return (
        <div className='large-container'>
            <h1 className='!text-xl md:!text-xl font-semibold text-center'>Inventory Reports</h1>

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

export default InvReportsMain