import React from "react";

const Heading = ({title}) => {
  return (
    <section className="text-center">
      <h1 className="font-semibold !text-xl">Indian Institue of Astrophysics</h1>
      <h2 className="font-semibold !text-lg">{title}</h2>
    </section>
  );
};

export default Heading;
