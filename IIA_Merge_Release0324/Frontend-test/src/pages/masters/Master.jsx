import React, { useEffect, useState } from 'react'
import FormContainer from '../../components/DKG_FormContainer'
import { Select } from 'antd'
import JobForm from './JobForm'
import WorkForm from './WorkForm'
import MaterialForm from './MaterialForm'
import { useLocation } from 'react-router-dom'
import VendorMasterForm from './VendorMaster'
import EmployeeMaster from './EmployeeMaster'

const masterDropDown = [
    {
        value: "Job",
        label: "Job"
    },
    // {
    //     value: "Work",
    //     label: "Work"
    // },
    {
        value: "Material",
        label: "Material"
    }, {
        value: "Vendor",
        label: "Vendor"
    },
    // {
    //     value: "Employee",
    //     label: "Employee"
    // },
]

const Master = () => {
    const [selectedMaster, setSelectedMaster] = useState("");
 // const [selectedMaster, setSelectedMaster] = useState(location?.state?.master || "");

    const handleChange = (value) => {
        setSelectedMaster(value);
    };

    const location = useLocation();
    const state = location?.state;

    const master = state?.master;
    const materialCode = state?.materialCode;
    const jobCode = state?.jobCode;

    
    

    const renderMasterForm = () => {
        switch (selectedMaster) {
            case "Job":
                return <JobForm jobCode={jobCode} />
            case "Work":
                return <WorkForm />
            case "Material":
                return <MaterialForm materialCode = {materialCode} />
            case "Vendor":
                return <VendorMasterForm />
            case "Employee":
               return <EmployeeMaster />
            default:
                return <div className='text-gray-400'>Select a master</div>
        }
    }

    useEffect(() => {
        if (master) {
            setSelectedMaster(master);
        }
    }, [master])
  return (
    <FormContainer>
        <h1 className="font-semibold text-center text-xl mb-4">Masters</h1>
        <div>
            <h2 className='font-semibold mb-1'>Select a master</h2>
        <Select options={masterDropDown} onChange={handleChange} className='w-32' value={selectedMaster} />
        </div>
        <div className="mt-4">
        {renderMasterForm()}
        </div>
    </FormContainer>
  )
}

export default Master
