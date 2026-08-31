import { Tabs } from 'antd'
import React from 'react'
import PendingGi from './PendingGi'
import GatePass from './GatePass'
import GiApprovalPage from './GiApprovalPage'
import GrnApproval from './GrnApproval'
import { useSelector } from "react-redux";
import GoodsTransferQueue from './GoodsTransferQueue'
import DemandAndIssueQueue from './DemandAndIssueQueue'
import AssetDispoaslQueue from './AssetDisposalQueue'
import PendingIssueNote from './pendingIssueNote'
import CycleCountQueue from './CycleCountQueue'

const Queue3 = () => {
const auth = useSelector((state) => state.auth);
const roleName=auth.role;
  return (
    <Tabs
      items={[
        {
          key: 'gi',
          label: 'Pending GI And Change Request GPRN',
          children: <PendingGi />,
        },
        ...(roleName === 'Store Purchase Officer' || roleName === 'Indent Creator' ? [{
          key: 'g',
          label: 'Pending GI',
          children: <GiApprovalPage />,
        }] : []),
        ...(roleName === 'Store Purchase Officer' || roleName === 'Store Person' ? [{
          key: 'grn',
          label: 'Pending GRN',
          children: <GrnApproval />,
        }] : []),
        {
          key: 'gatepass',
          label: 'Gate Pass',
          children: <GatePass />,
        },
        {
          key: 'gt',
          label: 'Goods Transfer',
          children: <GoodsTransferQueue />,
        },
        ...(roleName === 'Store Purchase Officer' ? [{
          key: 'Di',
          label: 'Pending Demand And issue',
          children: <DemandAndIssueQueue />,
        }] : []),
        ...(roleName === 'Store Purchase Officer' ? [{
          key: 'AD',
          label: 'Asset Disposal',
          children: <AssetDispoaslQueue />,
        }] : []),
        ...(roleName === 'Store Person' ? [{
          key: 'IN',
          label: 'Pending Issue Note',
          children: <PendingIssueNote />,
        }] : []),
         {
          key: 'CC',
          label: 'Cycle Count',
          children: <CycleCountQueue />,
        },
      ]}
    />
  )
}

export default Queue3
