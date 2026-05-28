import React, { useState, useEffect, useMemo } from "react";
import TableComponent from "../../../components/DKG_Table";
import { PlayCircleOutlined, FileTextOutlined, FileDoneOutlined, FileSearchOutlined } from '@ant-design/icons';

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
          {/* You can replace this hard-coded number with a dynamic value if needed */}
          <h3 className="font-semibold !text-2xl text-white text-left w-full">
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
        {/* Pass the filteredColumns to your TableComponent */}
        <TableComponent dataSource={tableData} columns={filteredColumns} />
      </section>
    </div>
  );
};

export default MainDashboard;