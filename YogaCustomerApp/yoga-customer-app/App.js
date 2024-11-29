import React from 'react';
import { NavigationContainer } from '@react-navigation/native';
import { createStackNavigator } from '@react-navigation/stack';
import HomeScreen from './screens/HomeScreen';
import ClassDetailsScreen from './screens/ClassDetailsScreen';
import CartScreen from './screens/CartScreen';
import ScheduleScreen from './screens/ScheduleScreen';

const Stack = createStackNavigator();

export default function App() {
    return (
        <NavigationContainer>
            <Stack.Navigator initialRouteName="Home">
                <Stack.Screen name="Home" component={HomeScreen} />
                <Stack.Screen name="ClassDetails" component={ClassDetailsScreen} />
                <Stack.Screen name="Cart" component={CartScreen} />
                <Stack.Screen name="Schedule" component={ScheduleScreen} />
            </Stack.Navigator>
        </NavigationContainer>
    );
}
