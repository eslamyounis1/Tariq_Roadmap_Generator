import React, { useState } from "react";
import axios from "axios";

function App() {
  const [topic, setTopic] = useState("");
  const [skills, setSkills] = useState([]);
  const [loading, setLoading] = useState(false);
  const [loadingSkill, setLoadingSkill] = useState(null); // Tracks loading for individual skill
  const [expandedSkill, setExpandedSkill] = useState(null);
  const [resources, setResources] = useState({}); // Stores resources per skill
  const [email, setEmail] = useState("");
  const [emailStatus, setEmailStatus] = useState(null); // Tracks email sending status

  const fetchSkills = async () => {
    if (!topic) {
      alert("Please enter a topic.");
      return;
    }
    setLoading(true);
    setResources({});
    setExpandedSkill(null);
    setEmailStatus(null);
    try {
      const response = await axios.post(
        "http://localhost:8080/api/v1/openai/generate-skills",
        { topic }
      );
      setSkills(response.data);
    } catch (error) {
      console.error("Error fetching skills:", error);
    }
    setLoading(false);
  };

  const fetchResources = async (skillName) => {
    if (resources[skillName]) {
      setExpandedSkill(skillName);
      return;
    }

    setLoadingSkill(skillName);
    try {
      const response = await axios.post(
        "http://localhost:8080/api/v1/openai/generate-resources",
        { skillName }
      );
      setResources((prevResources) => ({
        ...prevResources,
        [skillName]: response.data,
      }));
      setExpandedSkill(skillName);
    } catch (error) {
      console.error("Error fetching resources:", error);
    }
    setLoadingSkill(null);
  };

  const sendEmail = async () => {
    if (!email) {
      alert("Please enter your email address.");
      return;
    }
    setEmailStatus("loading");
    try {
      const response = await axios.post(
        "http://localhost:8080/api/v1/email/send-roadmap-email",
        {
          topic,
          email,
          skills: skills.map((skill) => ({
            name: skill.name,
            description: skill.description,
            resources: resources[skill.name] || [],
          })),
        }
      );
      setEmailStatus("success");
      console.log("Email sent successfully:", response.data);
    } catch (error) {
      console.error("Error sending email:", error);
      setEmailStatus("error");
    }
  };

  return (
    <div className="min-h-screen bg-gray-100 flex flex-col items-center p-6">
      <h1 className="text-4xl font-bold text-blue-600 mb-6">Tariq - Roadmap Generator</h1>

      {/* Topic Input and Search Button */}
      <div className="w-full max-w-xl bg-white shadow-lg rounded-lg p-6">
        <div className="flex items-center space-x-4">
          <input
            type="text"
            placeholder="Enter the topic you want to learn (e.g., Web Development)"
            className="flex-1 border rounded-lg px-4 py-2 focus:outline-none focus:ring-2 focus:ring-blue-500"
            value={topic}
            onChange={(e) => setTopic(e.target.value)}
          />
          <button
            onClick={fetchSkills}
            disabled={loading}
            className={`px-4 py-2 rounded-lg text-white ${
              loading ? "bg-gray-400" : "bg-blue-500 hover:bg-blue-600"
            }`}
          >
            {loading ? "Loading..." : "Search"}
          </button>
        </div>
      </div>

      {/* Skills and Resources */}
      <div className="w-full max-w-xl mt-6 space-y-4">
        {skills.map((skill, index) => (
          <div key={index} className="bg-white shadow-md rounded-lg p-4">
            <div className="flex justify-between items-center">
              <div>
                <h3 className="text-xl font-semibold text-gray-800">{skill.name}</h3>
                <p className="text-gray-600">{skill.description}</p>
              </div>
              <button
                onClick={() => fetchResources(skill.name)}
                disabled={loadingSkill === skill.name}
                className={`ml-4 px-4 py-2 rounded-lg text-white ${
                  loadingSkill === skill.name
                    ? "bg-gray-400"
                    : "bg-green-500 hover:bg-green-600"
                }`}
              >
                {loadingSkill === skill.name ? "Loading..." : "Get Resources"}
              </button>
            </div>

            {/* Dropdown for resources */}
            {expandedSkill === skill.name && resources[skill.name] && (
              <div className="mt-4 border-t pt-4">
                <h4 className="text-lg font-semibold text-gray-800">Learning Resources</h4>
                <ul className="mt-2 space-y-2">
                  {resources[skill.name].map((resource, idx) => (
                    <li key={idx}>
                      <a
                        href={resource.url}
                        target="_blank"
                        rel="noopener noreferrer"
                        className="text-blue-500 hover:underline font-medium"
                      >
                        {resource.title}
                      </a>
                      <span className="text-gray-600 ml-2">({resource.type})</span>
                    </li>
                  ))}
                </ul>
              </div>
            )}
          </div>
        ))}
      </div>

      {/* Email Section */}
      {skills.length > 0 && (
        <div className="w-full max-w-xl mt-6 bg-white shadow-lg rounded-lg p-6">
          <h2 className="text-lg font-semibold text-gray-800 mb-4">Send roadmap via email</h2>
          <div className="flex items-center space-x-4">
            <input
              type="email"
              placeholder="Enter your email address"
              className="flex-1 border rounded-lg px-4 py-2 focus:outline-none focus:ring-2 focus:ring-blue-500"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
            />
            <button
              onClick={sendEmail}
              disabled={emailStatus === "loading"}
              className={`px-4 py-2 rounded-lg text-white ${
                emailStatus === "loading"
                  ? "bg-gray-400"
                  : "bg-blue-500 hover:bg-blue-600"
              }`}
            >
              {emailStatus === "loading" ? "Sending..." : "Send Email"}
            </button>
          </div>
          {emailStatus === "success" && (
            <p className="text-green-600 mt-4">Email sent successfully!</p>
          )}
          {emailStatus === "error" && (
            <p className="text-red-600 mt-4">Failed to send email. Please try again.</p>
          )}
        </div>
      )}
    </div>
  );
}

export default App;
