import { useNavigate } from "react-router-dom";
import { useState } from "react";
import { Link } from "react-router-dom";
import LoginModal from "./components/LoginModel";

function HomePage() {
  const [isMenuOpen, setIsMenuOpen] = useState(false);
  const navigate = useNavigate();
  const [showLogin, setShowLogin] = useState(false);
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [isLoading, setIsLoading] = useState(false);

  const handleLogin = (e) => {
    e.preventDefault();
    setIsLoading(true);

    console.log(`email: ${email} and password: ${password}`);
    navigate("/design");

    setTimeout(() => {
      setIsLoading(false);
      setShowLogin(false);
    }, 1500);
  };

  return (
    <div className="min-h-screen bg-neutral-50 font-sans">
      {showLogin && (
        <LoginModal
          setShowLogin={setShowLogin}
          setEmail={setEmail}
          setPassword={setPassword}
          handleLogin={handleLogin}
          isLoading={isLoading}
          email={email}
          password={password}
        />
      )}

      {/* Navigation */}
      <nav className="bg-white shadow-lg fixed w-full z-50">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="flex justify-between items-center h-16">
            <div className="flex items-center">
              <div className="flex-shrink-0 flex items-center">
                <svg
                  className="h-8 w-8 text-indigo-600"
                  fill="none"
                  stroke="currentColor"
                  viewBox="0 0 24 24"
                >
                  <path
                    strokeLinecap="round"
                    strokeLinejoin="round"
                    strokeWidth="2"
                    d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z"
                  />
                </svg>
                <span className="ml-2 text-xl font-bold text-gray-900">
                  Furnish Studio
                </span>
              </div>
            </div>

            <div className="hidden md:flex items-center space-x-8">
              <a
                href="#features"
                className="text-gray-700 hover:text-indigo-600 transition-colors duration-300"
              >
                Features
              </a>
              <a
                href="#how-it-works"
                className="text-gray-700 hover:text-indigo-600 transition-colors duration-300"
              >
                How It Works
              </a>
              <a
                href="#gallery"
                className="text-gray-700 hover:text-indigo-600 transition-colors duration-300"
              >
                Gallery
              </a>
              <button
                onClick={() => setShowLogin(true)}
                className="px-4 py-2 bg-indigo-600 text-white rounded-full hover:bg-indigo-700 transition-colors duration-300 flex items-center gap-2"
              >
                <svg
                  className="h-4 w-4"
                  fill="none"
                  stroke="currentColor"
                  viewBox="0 0 24 24"
                >
                  <path
                    strokeLinecap="round"
                    strokeLinejoin="round"
                    strokeWidth="2"
                    d="M11 16l-4-4m0 0l4-4m-4 4h14"
                  />
                </svg>
                Login
              </button>
            </div>

            <div className="md:hidden flex items-center">
              <button
                onClick={() => setIsMenuOpen(!isMenuOpen)}
                className="p-2 rounded-md text-gray-700 hover:text-indigo-600"
              >
                <svg
                  className="h-6 w-6"
                  fill="none"
                  viewBox="0 0 24 24"
                  stroke="currentColor"
                >
                  {isMenuOpen ? (
                    <path
                      strokeLinecap="round"
                      strokeLinejoin="round"
                      strokeWidth={2}
                      d="M6 18L18 6M6 6l12 12"
                    />
                  ) : (
                    <path
                      strokeLinecap="round"
                      strokeLinejoin="round"
                      strokeWidth={2}
                      d="M4 6h16M4 12h16M4 18h16"
                    />
                  )}
                </svg>
              </button>
            </div>
          </div>
        </div>

        {isMenuOpen && (
          <div className="md:hidden bg-white shadow-lg">
            <div className="px-4 py-3 space-y-2">
              <a
                href="#features"
                className="block text-gray-700 hover:text-indigo-600"
              >
                Features
              </a>
              <a
                href="#how-it-works"
                className="block text-gray-700 hover:text-indigo-600"
              >
                How It Works
              </a>
              <a
                href="#gallery"
                className="block text-gray-700 hover:text-indigo-600"
              >
                Gallery
              </a>
              <button
                onClick={() => {
                  setShowLogin(true);
                  setIsMenuOpen(false);
                }}
                className="w-full px-4 py-2 bg-indigo-600 text-white rounded-full hover:bg-indigo-700 flex items-center justify-center gap-2"
              >
                <svg
                  className="h-4 w-4"
                  fill="none"
                  stroke="currentColor"
                  viewBox="0 0 24 24"
                >
                  <path
                    strokeLinecap="round"
                    strokeLinejoin="round"
                    strokeWidth="2"
                    d="M11 16l-4-4m0 0l4-4m-4 4h14"
                  />
                </svg>
                Designer Login
              </button>
            </div>
          </div>
        )}
      </nav>
      {/* Hero Section */}
      <div className="pt-20 bg-gradient-to-br from-indigo-50 to-white">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-16 grid lg:grid-cols-2 gap-12 items-center">
          <div className="text-center lg:text-left animate-slide-up">
            <h1 className="text-4xl sm:text-5xl lg:text-6xl font-bold text-gray-900 leading-tight">
              Craft Your <span className="text-indigo-600">Dream Space</span>
            </h1>
            <p className="mt-4 text-lg text-gray-600 max-w-md mx-auto lg:mx-0">
              Design and visualize furniture layouts with precision, bringing
              your perfect room to life in stunning 3D.
            </p>
            <div className="mt-8 flex flex-col sm:flex-row gap-4 justify-center lg:justify-start">
              <Link
                to="/design" // Changed from href="#" to to="/design"
                className="px-8 py-3 bg-indigo-600 text-white rounded-full hover:bg-indigo-700 transition-colors duration-300"
              >
                Get Started
              </Link>
              <a
                href="#how-it-works"
                className="px-8 py-3 bg-white text-indigo-600 border border-indigo-600 rounded-full hover:bg-indigo-50 transition-colors duration-300"
              >
                Learn More
              </a>
            </div>
          </div>
          <div className="relative">
            <img
              className="w-full h-96 object-cover rounded-2xl shadow-xl"
              src="https://images.unsplash.com/photo-1555041469-a586c61ea9bc?ixlib=rb-4.0.3&auto=format&fit=crop&w=1470&q=80"
              alt="Modern living room"
            />
            <div className="absolute inset-0 bg-gradient-to-t from-black/70 to-transparent rounded-2xl"></div>
          </div>
        </div>
      </div>
      {/* Features Section */}
      <div id="features" className="py-16 bg-white">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="text-center">
            <h2 className="text-indigo-600 font-semibold tracking-wide uppercase">
              Features
            </h2>
            <p className="mt-2 text-3xl sm:text-4xl font-bold text-gray-900">
              Design with Precision
            </p>
            <p className="mt-4 text-lg text-gray-600 max-w-2xl mx-auto">
              Powerful tools to create flawless furniture designs for any space.
            </p>
          </div>

          <div className="mt-12 grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-8">
            {[
              {
                icon: "PanelLeft",
                title: "Room Customization",
                desc: "Define room dimensions, shapes, and colors for a realistic design canvas.",
                img: "https://images.unsplash.com/photo-1618221195710-dd6b41faaea6?ixlib=rb-4.0.3&auto=format&fit=crop&w=1400&q=80",
              },
              {
                icon: "LampDesk",
                title: "3D Visualization",
                desc: "Instantly view your 2D designs in immersive 3D for a true-to-life experience.",
                img: "https://images.unsplash.com/photo-1600210492493-0946911123ea?ixlib=rb-4.0.3&auto=format&fit=crop&w=1374&q=80",
              },
              {
                icon: "Scaling",
                title: "Perfect Scaling",
                desc: "Scale furniture to fit any room perfectly, ensuring balanced layouts.",
                img: "https://images.unsplash.com/photo-1583847268964-b28dc8f51f92?ixlib=rb-4.0.3&auto=format&fit=crop&w=1374&q=80",
              },
              {
                icon: "ShoppingBag",
                title: "Furniture Customization",
                desc: "Personalize colors, textures, and finishes for every piece.",
                img: "https://images.unsplash.com/photo-1618220179428-22790b461013?ixlib=rb-4.0.3&auto=format&fit=crop&w=1527&q=80",
              },
              {
                icon: "Save",
                title: "Save & Edit Designs",
                desc: "Store your designs securely and edit them anytime with ease.",
                img: "https://images.unsplash.com/photo-1600585152220-90363fe7e115?ixlib=rb-4.0.3&auto=format&fit=crop&w=1470&q=80",
              },
              {
                icon: "Users",
                title: "Designer Accounts",
                desc: "Secure access for designers to manage and create stunning layouts.",
                img: "https://images.unsplash.com/photo-1551434678-e076c223a692?ixlib=rb-4.0.3&auto=format&fit=crop&w=1470&q=80",
              },
            ].map((feature, index) => (
              <div
                key={index}
                className="relative bg-gray-50 rounded-2xl p-6 shadow-lg hover:shadow-xl transition-shadow duration-300 animate-slide-up"
              >
                <div className="absolute -top-4 left-6 bg-indigo-600 text-white rounded-full p-2">
                  <svg
                    className="h-6 w-6"
                    fill="none"
                    stroke="currentColor"
                    viewBox="0 0 24 24"
                  >
                    <path
                      strokeLinecap="round"
                      strokeLinejoin="round"
                      strokeWidth="2"
                      d={
                        feature.icon === "PanelLeft"
                          ? "M4 5h16M4 11h16M4 17h16"
                          : feature.icon === "LampDesk"
                          ? "M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z"
                          : feature.icon === "Scaling"
                          ? "M3 3h18M3 21h18M3 12h18"
                          : feature.icon === "ShoppingBag"
                          ? "M16 11V7a4 4 0 00-8 0v4M5 9h14l1 12H4L5 9z"
                          : feature.icon === "Save"
                          ? "M8 7H5a2 2 0 00-2 2v9a2 2 0 002 2h14a2 2 0 002-2V9a2 2 0 00-2-2h-3m-1 4l-3 3m0 0l-3-3m3 3V4"
                          : "M12 4.5v15m7.5-7.5h-15"
                      }
                    />
                  </svg>
                </div>
                <h3 className="mt-8 text-lg font-semibold text-gray-900">
                  {feature.title}
                </h3>
                <p className="mt-2 text-gray-600">{feature.desc}</p>
                <img
                  src={feature.img}
                  alt={feature.title}
                  className="mt-4 rounded-lg shadow-sm w-full h-40 object-cover"
                />
              </div>
            ))}
          </div>
        </div>
      </div>
      {/* How It Works Section */}
      <div
        id="how-it-works"
        className="py-16 bg-gradient-to-br from-indigo-50 to-white"
      >
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="text-center">
            <h2 className="text-indigo-600 font-semibold tracking-wide uppercase">
              How It Works
            </h2>
            <p className="mt-2 text-3xl sm:text-4xl font-bold text-gray-900">
              Streamlined Design Process
            </p>
            <p className="mt-4 text-lg text-gray-600 max-w-2xl mx-auto">
              Transform your ideas into reality in four simple steps.
            </p>
          </div>

          <div className="mt-12 grid grid-cols-1 md:grid-cols-2 gap-8">
            {[
              {
                step: 1,
                title: "Enter Room Details",
                desc: "Input dimensions, shapes, and colors to create your virtual room.",
                img: "https://images.unsplash.com/photo-1616486338812-3dadae4b4ace?ixlib=rb-4.0.3&auto=format&fit=crop&w=1632&q=80",
              },
              {
                step: 2,
                title: "Create 2D Design",
                desc: "Arrange furniture from our catalog in an intuitive 2D interface.",
                img: "https://images.unsplash.com/photo-1617806118233-18e1de247200?ixlib=rb-4.0.3&auto=format&fit=crop&w=1632&q=80",
              },
              {
                step: 3,
                title: "View in 3D",
                desc: "Switch to 3D to visualize your design in a realistic setting.",
                img: "https://images.unsplash.com/photo-1616486338812-3dadae4b4ace?ixlib=rb-4.0.3&auto=format&fit=crop&w=1632&q=80",
              },
              {
                step: 4,
                title: "Customize & Refine",
                desc: "Fine-tune colors, textures, and scaling for a perfect fit.",
                img: "https://images.unsplash.com/photo-1600210491892-03d54c0aaf87?ixlib=rb-4.0.3&auto=format&fit=crop&w=1470&q=80",
              },
            ].map((step, index) => (
              <div
                key={index}
                className="relative bg-white rounded-2xl p-6 shadow-lg hover:shadow-xl transition-shadow duration-300 animate-slide-up"
              >
                <div className="absolute top-6 left-6 bg-indigo-600 text-white rounded-full h-10 w-10 flex items-center justify-center text-lg font-bold">
                  {step.step}
                </div>
                <h3 className="ml-16 text-lg font-semibold text-gray-900">
                  {step.title}
                </h3>
                <p className="ml-16 mt-2 text-gray-600">{step.desc}</p>
                <img
                  src={step.img}
                  alt={step.title}
                  className="mt-4 rounded-lg shadow-sm w-full h-48 object-cover"
                />
              </div>
            ))}
          </div>
        </div>
      </div>
      {/* Gallery Section */}
      <div id="gallery" className="py-16 bg-white">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="text-center">
            <h2 className="text-indigo-600 font-semibold tracking-wide uppercase">
              Gallery
            </h2>
            <p className="mt-2 text-3xl sm:text-4xl font-bold text-gray-900">
              Inspiration Gallery
            </p>
            <p className="mt-4 text-lg text-gray-600 max-w-2xl mx-auto">
              Explore stunning designs to spark your creativity.
            </p>
          </div>

          <div className="mt-12 grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-8">
            {[
              {
                title: "Modern Living Room",
                desc: "Sleek and contemporary elegance for your living space.",
                img: "https://images.unsplash.com/photo-1586023492125-27b2c045efd7?ixlib=rb-4.0.3&auto=format&fit=crop&w=1558&q=80",
                tags: ["Contemporary", "Neutral Tones"],
              },
              {
                title: "Spacious Dining Area",
                desc: "Ideal for family gatherings and entertaining.",
                img: "https://images.unsplash.com/photo-1600121848594-d8644e57abab?ixlib=rb-4.0.3&auto=format&fit=crop&w=1470&q=80",
                tags: ["Family Style", "Warm Tones"],
              },
              {
                title: "Home Office",
                desc: "Ergonomic and minimalist workspace for productivity.",
                img: "https://images.unsplash.com/photo-1497366811353-6870744d04b2?ixlib=rb-4.0.3&auto=format&fit=crop&w=1469&q=80",
                tags: ["Minimalist", "Productivity"],
              },
            ].map((item, index) => (
              <div
                key={index}
                className="bg-white rounded-2xl overflow-hidden shadow-lg hover:shadow-xl transition-shadow duration-300 animate-slide-up"
              >
                <img
                  className="w-full h-64 object-cover"
                  src={item.img}
                  alt={item.title}
                />
                <div className="p-6">
                  <h3 className="text-lg font-semibold text-gray-900">
                    {item.title}
                  </h3>
                  <p className="mt-2 text-gray-600">{item.desc}</p>
                  <div className="mt-4 flex gap-2">
                    {item.tags.map((tag, i) => (
                      <span
                        key={i}
                        className="px-2 py-1 text-xs font-semibold rounded-full bg-indigo-100 text-indigo-800"
                      >
                        {tag}
                      </span>
                    ))}
                  </div>
                </div>
              </div>
            ))}
          </div>

          <div className="mt-12 text-center">
            <a
              href="#"
              className="px-6 py-3 bg-indigo-600 text-white rounded-full hover:bg-indigo-700 transition-colors duration-300"
            >
              View All Designs
            </a>
          </div>
        </div>
      </div>
      {/* CTA Section */}
      <div className="bg-gradient-to-r from-indigo-700 to-indigo-900 py-16">
        <div className="max-w-2xl mx-auto text-center px-4 sm:px-6 lg:px-8">
          <h2 className="text-3xl sm:text-4xl font-bold text-white">
            Ready to Design Your Future?
          </h2>
          <p className="mt-4 text-lg text-indigo-100">
            Create breathtaking furniture layouts that bring visions to life.
          </p>
          <a
            href="#"
            className="mt-8 inline-flex items-center px-6 py-3 bg-white text-indigo-700 rounded-full hover:bg-indigo-50 transition-colors duration-300"
          >
            <svg
              className="h-5 w-5 mr-2"
              fill="none"
              stroke="currentColor"
              viewBox="0 0 24 24"
            >
              <path
                strokeLinecap="round"
                strokeLinejoin="round"
                strokeWidth="2"
                d="M11 16l-4-4m0 0l4-4m-4 4h14"
              />
            </svg>
            Sign In as Designer
          </a>
        </div>
      </div>
      {/* Footer */}
      <footer className="bg-gray-900 py-12">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="flex flex-col md:flex-row justify-between items-center">
            <div className="flex items-center">
              <svg
                className="h-8 w-8 text-white"
                fill="none"
                stroke="currentColor"
                viewBox="0 0 24 24"
              >
                <path
                  strokeLinecap="round"
                  strokeLinejoin="round"
                  strokeWidth="2"
                  d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z"
                />
              </svg>
              <span className="ml-2 text-xl font-bold text-white">
                Furnish Studio
              </span>
            </div>
            <p className="mt-4 md:mt-0 text-gray-400 text-sm">
              © 2025 Furnish Studio. All rights reserved.
            </p>
          </div>
          <div className="mt-8 border-t border-gray-700 pt-8 flex flex-col md:flex-row justify-between items-center">
            <div className="flex gap-6">
              <a href="#" className="text-gray-400 hover:text-white">
                <svg
                  className="h-6 w-6"
                  fill="currentColor"
                  viewBox="0 0 24 24"
                >
                  <path
                    fillRule="evenodd"
                    d="M22 12c0-5.523-4.477-10-10-10S2 6.477 2 12c0 4.991 3.657 9.128 8.438 9.878v-6.987h-2.54V12h2.54V9.797c0-2.506 1.492-3.89 3.777-3.89 1.094 0 2.238.195 2.238.195v2.46h-1.26c-1.243 0-1.63.771-1.63 1.562V12h2.773l-.443 2.89h-2.33v6.988C18.343 21.128 22 16.991 22 12z"
                    clipRule="evenodd"
                  />
                </svg>
              </a>
              <a href="#" className="text-gray-400 hover:text-white">
                <svg
                  className="h-6 w-6"
                  fill="currentColor"
                  viewBox="0 0 24 24"
                >
                  <path
                    fillRule="evenodd"
                    d="M12.315 2c2.43 0 2.784.013 3.808.06 1.064.049 1.791.218 2.427.465a4.902 4.902 0 011.772 1.153SEND 4.902 4.902 0 011.153 1.772c.247.636.416 1.363.465 2.427.048 1.067.06 1.407.06 4.123v.08c0 2.643-.012 2.987-.06 4.043-.049 1.064-.218 1.791-.465 2.427a4.902 4.902 0 01-1.153 1.772 4.902 4.902 0 01-1.772 1.153c-.636.247-1.363.416-2.427.465-1.067.048-1.407.06-4.123.06h-.08c-2.643 0-2.987-.012-4.043-.06-1.064-.049-1.791-.218-2.427-.465a4.902 4.902 0 01-1.772-1.153 4.902 4.902 0 01-1.153-1.772c-.247-.636-.416-1.363-.465-2.427-.047-1.024-.06-1.379-.06-3.808v-.63c0-2.43.013-2.784.06-3.808.049-1.064.218-1.791.465-2.427a4.902 4.902 0 011.153-1.772A4.902 4.902 0 015.45 2.525c.636-.247 1.363-.416 2.427-.465C8.901 2.013 9.256 2 11.685 2h.63zm-.081 1.802h-.468c-2.456 0-2.784.011-3.807.058-.975.045-1.504.207-1.857.344-.467.182-.8.398-1.15.748-.35.35-.566.683-.748 1.15-.137.353-.3.882-.344 1.857-.047 1.023-.058 1.351-.058 3.807v.468c0 2.456.011 2.784.058 3.807.045.975.207 1.504.344 1.857.182.466.399.8.748 1.15.35.35.683.566 1.15.748.353.137.882.3 1.857.344 1.054.048 1.37.058 4.041.058h.08c2.597 0 2.917-.01 3.96-.058.976-.045 1.505-.207 1.858-.344.466-.182.8-.398 1.15-.748.35-.35.566-.683.748-1.15.137-.353.3-.882.344-1.857.048-1.055.058-1.37.058-4.041v-.08c0-2.597-.01-2.917-.058-3.96-.045-.976-.207-1.505-.344-1.858a3.097 3.097 0 00-.748-1.15 3.098 3.098 0 00-1.15-.748c-.353-.137-.882-.3-1.857-.344-1.023-.047-1.351-.058-3.807-.058zM12 6.865a5.135 5.135 0 110 10.27 5.135 5.135 0 010-10.27zm0 1.802a3.333 3.333 0 100 6.666 3.333 3.333 0 000-6.666zm5.338-3.205a1.2 1.2 0 110 2.4 1.2 1.2 0 010-2.4z"
                    clipRule="evenodd"
                  />
                </svg>
              </a>
              <a href="#" className="text-gray-400 hover:text-white">
                <svg
                  className="h-6 w-6"
                  fill="currentColor"
                  viewBox="0 0 24 24"
                >
                  <path d="M8.29 20.251c7.547 0 11.675-6.253 11.675-11.675 0-.178 0-.355-.012-.53A8.348 8.348 0 0022 5.92a8.19 8.19 0 01-2.357.646 4.118 4.118 0 001.804-2.27 8.224 8.224 0 01-2.605.996 4.107 4.107 0 00-6.993 3.743 11.65 11.65 0 01-8.457-4.287 4.106 4.106 0 001.27 5.477A4.072 4.072 0 012.8 9.713v.052a4.105 4.105 0 003.292 4.022 4.095 4.095 0 01-1.853.07 4.108 4.108 0 003.834 2.85A8.233 8.233 0 012 18.407a11.616 11.616 0 006.29 1.84" />
                </svg>
              </a>
            </div>
            <nav className="mt-4 md:mt-0 flex gap-6">
              <a href="#" className="text-gray-400 hover:text-white text-sm">
                Privacy
              </a>
              <a href="#" className="text-gray-400 hover:text-white text-sm">
                Terms
              </a>
              <a href="#" className="text-gray-400 hover:text-white text-sm">
                Contact
              </a>
            </nav>
          </div>
        </div>
      </footer>
    </div>
  );
}

export default HomePage;
