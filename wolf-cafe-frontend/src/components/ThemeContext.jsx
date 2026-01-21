// src/components/ThemeContext.jsx
import React, { createContext, useState, useEffect } from "react";

/**
 * ThemeContext
 * Provides the current theme and a setter function to the app.
 * Supported themes:
 *  - light
 *  - dark
 *  - ncstate
 *  - vaporwave
 */
export const ThemeContext = createContext();

export const ThemeProvider = ({ children }) => {
  const [theme, setTheme] = useState("light"); // default theme

  /**
   * setThemeByName
   * Updates the current theme and saves it to localStorage
   * @param {string} themeName - Name of the theme to set
   */
  const setThemeByName = (themeName) => {
    setTheme(themeName);
    localStorage.setItem("appTheme", themeName);
  };

  /**
   * Load theme from localStorage on mount
   * so user's selection persists across refresh
   */
  useEffect(() => {
    const stored = localStorage.getItem("appTheme");
    if (stored) setTheme(stored);
  }, []);

  /**
   * Apply theme class to <body>
   * Also add smooth transition for background and text colors
   */
  useEffect(() => {
    // clear existing theme classes
    document.body.className = "";
    document.body.classList.add(`theme-${theme}`);

    // add smooth transition for background and text color
    document.body.style.transition = "background-color 0.3s, color 0.3s";
  }, [theme]);

  return (
    <ThemeContext.Provider value={{ theme, setThemeByName }}>
      {children}
    </ThemeContext.Provider>
  );
};
