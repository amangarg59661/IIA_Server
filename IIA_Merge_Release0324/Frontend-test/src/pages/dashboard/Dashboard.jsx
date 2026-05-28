import React, { useContext,  useEffect } from 'react'
import { Input } from 'antd';
import { ActiveTabContext } from '../../context/dashboardActiveTabContext';
import Form2 from './approvedVendor/Form2';
// import Form2 from './indentApproval/Form2';
// import Form3 from './indentModification/Form3';
// import Form4 from './tenderRequest/Form4';
// import Form5 from './tenderEvaluation/Form5';
// import Form6 from './communityNomination/Form6';
// import Form7 from './purchaseOrder/Form7';
// // import Form8 from './approvalWorkflow/Form8';
// import Form9 from './performanceWarranty/Form9';
// import Form10 from './deliveryTracking/Form10';
// import Form11 from './goodsProvisionalRecieptNote/Form11';
// import Form12 from './goodsInspection/Form12';
// import Form13 from './goodsReturn/Form13';
// import Form14 from './goodsReceiptInspection/Form14';
// import Form15 from './assterMaster/Form15';
// import Form16 from './goodsIssue/Form16';
// import Form17 from './goodsTransfer/Form17';
// import Form18 from './materialDisposal/Form18';
// import Form19 from './gatePass/Form19';
// import Form20 from './demandAndIssue/Form20';
// import Form7a from './serviceOrder/Form7a';
// import Form7b from './contingencyPurchase/Form7b';
// import Queue from './queue/Queue';
//import MainDashboard from './Dashboard/MainDashboard';
// import NewCalibrationForm from './duty/calibration/newCalibration/NewCalibrationForm';
// import Form4a from './tenderRequest/Form4a';
// import MaterialForm from './materialDetails/MaterialForm';
// import Temp from './goodsProvisionalRecieptNote/GPRN';
// import GPRN from './goodsProvisionalRecieptNote/GPRN';
// import GoodsInspection from './goodsInspection/GoodsInspection';
// import JobCreation from './jobCreation/JobCreation';
// import WorkCreation from './workCreation/WorkCreation';
// import Form21 from './lineItems/Form21';
import Form1 from "../dashboard/vendorRegistration/Form1"
import MainDashboard from './newDashboard/MainDashboard';


// const { Search } = Input;


const dashboardTabItems = [
//   {
//     id: 1,
//     title: 'Indent Creation Form',
//     // icon: <HomeOutlined />
//     icon: <FileTextOutlined />
//   },
//   {
//     id: 2,
//     title: 'Indent Approval Form',
//     // icon: <IdcardOutlined />
//     icon: <FileTextOutlined />
//   },
//   {
//     id: 3,
//     title: 'Indent Modification Form',
//     icon: <FileTextOutlined />
//   },
//   {
//     id: 4,
//     title: 'Tender Request Form',
//     // icon: <RobotOutlined />
//     icon: <FileTextOutlined />
//   },
//   {
//     id: 5,
//     title: 'Tender Evaluation Input Form',
//     // icon: <LineChartOutlined />,
//     icon: <FileTextOutlined />
//   },
// //   {
// //     id: 6,
// //     title: 'ISO Reports',
// //     icon: <ProfileOutlined />
// //   },
// //   {
// //     id: 7,
// //     title: 'Admin',
// //     icon: <UserOutlined />
// //   },
]

const Dashboard = () => {
  // const [activeTab, setActiveTab] = useState(1)
  const {activeTab, setActiveTab} = useContext(ActiveTabContext)
  
  
    //Set activeTab to 1 when Dashboard mounts
    useEffect(() => {
      setActiveTab(1);
    }, []);
  

  const renderDashboardTabItems = () => 
    dashboardTabItems.map(item=> {
      return (
        <div 
          key={item.id} 
          onClick={() => setActiveTab(item.id)}
          className={`cursor-pointer ${activeTab === item.id ? 'border-b-2 border-pink' : ''}`}
        >
          <span className='dashboard-tab-icon'>{item.icon}</span> <br />
          <div className='text-center w-full'>{item.title}</div>
          </div>
      )
    })
/*
    const renderTab = () => {
      switch (activeTab){
        // case 2:
        //     return <Form2 />
        case 1:
            return <MainDashboard />
        case 3:
          return <Form1 />
        //case 4:
        //   return <Form2 />
        // case 5:
        //   return <Form3 />
        // case 6:
        //   return <Form4 />
        // case 7:
        //   return <Form4a />
        // case 8:
        //   return <Form6 />
        // case 9:
        //   return <Form7 />
        // case 10:
        //     return <Form8/>
        // case 11:
        //     return <Form9 />
        // case 12:
        //     return <Form10 />
        // case 13:
        //     return <GPRN />
        // case 14:
        //     return <GoodsInspection />
        // case 15:
        //     return <Form13 />
        // case 16:
        //     return <Form14 />
        // case 17:
        //     return <Form15 />
        // case 18:
        //     return <Form16 />
        // case 19:
        //     return <Form17 />
        // case 20:
        //     return <Form18 />
        // case 21:
        //     return <Form19 />
        // case 22:
        //     return <Form20 />
        // case 23:
        //     return <Form7a />
        // case 24:
        //     return <Form7b />
        // case 25:
        //     return <MaterialForm />
        // case 26:
        //     return <JobCreation />
        // case 27:
        //     return <WorkCreation />
        default:
          break
      }
    }
*/
const renderTab = () => {
  switch (activeTab){
    case 1:
      return (
        <>
           { /*<MainDashboard/> */}
          <Form1 />           
        </>
      );
    case 3:
      return <Form1 />;
    default:
      break;
  }
};

  return (
    <div className='flex flex-col mt-15 gap-4 md:gap-8 bg-white p-2 w-full md:w-4/5 mx-auto h-[100vh] md:h-fit overflow-y-auto'>
    {/* <section>
      <Search placeholder='Search' className='dashboard-search' />
    </section> */}
    <section>
    {/* <div className="dashboard-tabs grid grid-cols-4 gap-4 bg-darkBlue rounded text-offWhite p-4">
      {renderDashboardTabItems()}
    </div> */}
    </section>
    <section>
      {renderTab()}
    </section>
    </div>
  )
}

export default Dashboard