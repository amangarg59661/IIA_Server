import React, { useState, useEffect, useCallback } from 'react';
import { Spin } from 'antd';
import QueueRequest from './QueueRequest';
import axios from 'axios';
import { useSelector } from 'react-redux';

/**
 * Cycle Count lives on the generic workflow engine (workflowId 12), same as
 * Indent/PO/SO/PV/SI — but sits in the Inventory tab (Queue3) rather than
 * the Procurement tab (Queue1), since it's a stock-count concern. Queue3's
 * other tabs are each their own self-fetching component (DemandAndIssueQueue,
 * AssetDisposalQueue, PendingIssueNote, etc.), so this follows that same
 * shape rather than plugging into Queue1's combined fetch-and-split setup.
 *
 * NOTE: not role-gated below — add a role check here (matching how the other
 * Queue3 tabs restrict by roleName) once you know which role(s) should see
 * Cycle Count approvals.
 */
const CycleCountQueue = () => {
  const auth = useSelector((state) => state.auth);
  const { userId } = useSelector((state) => state.auth);

  const [ccData, setCcData] = useState([]);
  const [loading, setLoading] = useState(false);

  const fetchData = useCallback(async () => {
    if (!auth?.role) return;
    setLoading(true);
    try {
      const params = new URLSearchParams();
      params.append('roleName', auth.role);
      if (userId) params.append('userId', userId);

      const response = await axios.get(`/pendingWorkflowTransitionQueue?${params.toString()}`);
      const responseData = response.data.responseData || [];

      // Same formatting shape Queue1 uses for its per-workflowId spreads,
      // scoped down to just workflowId 12 (Cycle Count) here.
      const formatted = responseData
        .filter((item) => item.workflowId === 12)
        .map((item) => ({
          key: item.requestId,
          requestId: item.requestId,
          workflowId: item.workflowId,
          workflowName: item.workflowName,
          createdDate: new Date(item.createdDate),
          remarks: item.transitionHistory?.[0]?.remarks || 'No remarks',
          status: item.nextAction,
          action: item.action,
          workflowTransitionId: item.workflowTransitionId,
          assignedToUserId: item.assignedToUserId,
          assignedToEmployeeName: item.assignedToEmployeeName,
          countType: item.countType,
          locatorId: item.locatorId,
          totalVarianceValue: item.totalVarianceValue,
        }))
        .sort((a, b) => b.createdDate - a.createdDate);

      setCcData(formatted);
    } catch (error) {
      console.error('CycleCountQueue fetchData error:', error);
    } finally {
      setLoading(false);
    }
  }, [auth?.role, userId]);

  useEffect(() => {
    fetchData();
  }, [fetchData]);

  if (loading) {
    return (
      <Spin
        size="large"
        tip="Loading cycle counts..."
        style={{ marginTop: 48, display: 'block' }}
      />
    );
  }

  return (
    <QueueRequest
      workflowId={12}
      data={ccData}
      loading={loading}
      refetchData={fetchData}
    />
  );
};

export default CycleCountQueue;
