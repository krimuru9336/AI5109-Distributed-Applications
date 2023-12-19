import React from "react";

import {
  MD3LightTheme as DefaultTheme,
  PaperProvider,
} from "react-native-paper";
import Greeting from "./Greeting";

const theme = {
  ...DefaultTheme,
  colors: {
    ...DefaultTheme.colors,
    primary: "#fffbdb",
    secondary: "#ffec51",
  },
};

export default function App() {
  return (
    <PaperProvider theme={theme}>
      <Greeting />
    </PaperProvider>
  );
}
