import React, { useCallback, useEffect, useState } from 'react'
import { apiCall } from '../../../utils/CommonFunctions'
import { useSelector } from 'react-redux'
import { Button, Input, message, Table } from 'antd'
import dayjs from 'dayjs'
import Btn from '../../../components/DKG_Btn'
import { useNavigate } from 'react-router-dom'

const SubworkflowTransition = () => {
    const {userId} = useSelector(state => state.auth)
    const [queueData, setQueueData] = useState([])
    const [searchTerm, setSearchTerm] = useState('')
    const [appliedSearchTerm, setAppliedSearchTerm] = useState('')
    const navigate = useNavigate();

    const handleRowClick = (record) => {
        navigate("/procurement/tender/evaluation", { state: { requestId: record.requestId}})
    }

    const handleApprove = async (subWorkflowTransitionId) => {
        try{
            const {data} = await apiCall("POST", `approveSubWorkflow?subWorkflowTransitionId=${subWorkflowTransitionId}`)
            message.success("Approval successful")
                populateData()
        }catch(err){
            
        }
    }

    const columns = [
        {
            title: 'Indent ID',
            dataIndex: 'indentId',
            key: 'indentId',
        },
        {
            title: 'Request ID',
            dataIndex: 'requestId',
            key: 'requestId',
        },
        {
            title: 'Indentor Name',
            dataIndex: 'indentorName',
            key: 'indentorName',
        },
        {
            title: 'Project Name',
            dataIndex: 'projectName',
            key: 'projectName',
        },
        {
            title: 'Consignee',
            dataIndex: 'consignee',
            key: 'consignee',
        },
        {
            title: 'Workflow Name',
            dataIndex: 'workflowName',
            key: 'workflowName',
        },
        {
            title: 'Status',
            dataIndex: 'status',
            key: 'status',
        },
        {
            title: 'Action',
            dataIndex: 'action',
            key: 'action',
        },
        {
            title: 'Created Date',
            dataIndex: 'createdDate',
            key: 'createdDate',
            render: (text) => text ? dayjs(text).format('DD/MM/YYYY') : '-'
        },
        {
            title: 'Modified Date',
            dataIndex: 'modificationDate',
            key: 'modificationDate',
            render: (text) => text ? dayjs(text).format('DD/MM/YYYY') : '-'
        },
        {
            title: "Actions",
            dataIndex: "actions",
            render: (_, record) =>(
                <div className='flex gap-2 items-center'>
                    <Btn onClick={() => handleRowClick(record)}>Upload</Btn>
                    <Button className='border-darkBlue hover:border-darkBlueHover' onClick={() => handleApprove(record.subWorkflowTransitionId)}>Approve</Button>
                </div>
            ) 
        }
    ]

    const populateData = useCallback(async () => {
        try{
            const {data} = await apiCall("GET", `getSubWorkflowTransitionQueue?updatedBy=${userId}`)
           // setQueueData(data.responseData || [])
             // Filter only pending action records
        const allData = data.responseData || [];
        const pendingData = allData.filter(item => item.status?.toLowerCase() === 'pending');

        setQueueData(pendingData);
        }
        catch(error){
            console.error("Error fetching queue data:", error)
        }
    }, [userId])

    const handleSearch = () => {
        setAppliedSearchTerm(searchTerm.trim())
    }

    const handleClear = () => {
        setSearchTerm('')
        setAppliedSearchTerm('')
    }

    const filteredData = appliedSearchTerm 
        ? queueData.filter(item => 
            item.indentId?.toLowerCase().includes(appliedSearchTerm.toLowerCase()) ||
            item.requestId?.toLowerCase().includes(appliedSearchTerm.toLowerCase())
          )
        : queueData

    useEffect(() => {
        populateData()
    }, [populateData])

    return (
        <div className="p-4">
            <div className="flex gap-2 mb-4">
                <Input
                    placeholder="Search by Indent ID or Request ID"
                    value={searchTerm}
                    onChange={(e) => setSearchTerm(e.target.value)}
                    className="max-w-xs"
                />
                <Button type="primary" onClick={handleSearch}>
                    Search
                </Button>
                <Button onClick={handleClear}>
                    Clear
                </Button>
            </div>
            <Table 
                columns={columns} 
                dataSource={filteredData}
                rowKey="subWorkflowTransitionId"
                scroll={{ x: 'max-content' }}
            />
        </div>
    )
}

export default SubworkflowTransition
