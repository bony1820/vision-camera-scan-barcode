import * as React from 'react';

import { StyleSheet, View, Text, Button } from 'react-native';
import { scanCodeFromLibrary } from 'vision-camera-scan-barcode';

export default function App() {
  const [result, setResult] = React.useState<number[]>([]);

  const selectImage = () => {
    scanCodeFromLibrary()
      .then(setResult)
      .catch(console.log)
  }

  return (
    <View style={styles.container}>
      <Text>List code:</Text>
      {
        result.map(item => <Text key={item}>{item}</Text>)
      }
      <Button title={"Select Image"} onPress={selectImage}/>
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    alignItems: 'center',
    justifyContent: 'center',
  },
});
