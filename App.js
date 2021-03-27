/**
 * Sample React Native App
 * https://github.com/facebook/react-native
 *
 * @format
 * @flow strict-local
 */

import React, {useEffect,useState} from 'react';
import {
  requireNativeComponent,
  View,
  Dimensions,
  Text,
  StyleSheet,
  TouchableOpacity,
} from 'react-native';

import TextureView from './TextureView';
import NativeCameraModule from './CustomModule';

const windowWidth = Dimensions.get('window').width;

const App = () => {


  const [click,setClick]=useState(false)
  const [url,setUrl]=useState("")

  useEffect(() => {
    // setTimeout(() => {
    //   NativeCameraModule.videoStartCapture().then(res=>{
    //     setUrl(res);
    //     console.log(url);
    //   })
    //   setClick(!click);
    // }, 500);
  }, []);
  
  return (
    <View style={styles.container}>
      {<TextureView style={{width: '100%', height: windowWidth*(4/3)}}/>}
      <View style={{flexDirection: 'row', justifyContent: 'center',paddingVertical:10,alignItems:"center"}}>
        {click?<TouchableOpacity
          style={{paddingHorizontal: 20, paddingVertical: 10,backgroundColor:"#555",borderRadius:20}}
          onPress={() => {
            console.log('Button stop');
            console.log(NativeCameraModule.videoStopCapture());
            setClick(!click);
          }}>
          <Text style={{color: 'white'}}>Stop</Text>
        </TouchableOpacity>:<TouchableOpacity
          style={{paddingHorizontal: 20, paddingVertical: 10,backgroundColor:"#555",borderRadius:20}}
          onPress={() => {
            console.log('Button start');
            //NativeCameraModule.mySuperDuperFunction();
            // TextureView.videoStartCapture();
            NativeCameraModule.videoStartCapture().then(res=>{
              setUrl(res);
              console.log(url);
            })

            setClick(!click);
          }}>
          <Text style={{color: 'white'}}>Start</Text>
        </TouchableOpacity>}
      </View>
    </View>
  );
};

const styles = StyleSheet.create({
  container: {
    //padding: 1,
    backgroundColor: 'black',
    flex: 1,
    height: '100%',
    flexDirection:"column",
    justifyContent:"center"
  },
});

export default App;
