import React, { useState } from 'react';
import { View, Text, Button, Alert, StyleSheet } from 'react-native';
import AsyncStorage from '@react-native-async-storage/async-storage';

export default function ClassDetailsScreen({ route }) {
    const { classData } = route.params;

    const addToCart = async () => {
        try {
            const cart = JSON.parse(await AsyncStorage.getItem('cart')) || [];
            cart.push(classData);
            await AsyncStorage.setItem('cart', JSON.stringify(cart));
            Alert.alert('Success', 'Class added to cart!');
        } catch (error) {
            Alert.alert('Error', 'Could not add class to cart.');
        }
    };

    return (
        <View style={styles.container}>
            <Text style={styles.title}>{classData.type}</Text>
            <Text>Day: {classData.dayOfWeek}</Text>
            <Text>Time: {classData.time}</Text>
            <Text>Price: £{classData.price}</Text>
            <Text>Capacity: {classData.capacity} people</Text>
            <Text>Duration: {classData.duration} minutes</Text>
            <Text>Description: {classData.description || 'No description provided.'}</Text>
            <Button title="Add to Cart" onPress={addToCart} />
        </View>
    );
}

const styles = StyleSheet.create({
    container: {
        flex: 1,
        padding: 20,
        backgroundColor: '#f5f5f5',
    },
    title: {
        fontSize: 24,
        fontWeight: 'bold',
        marginBottom: 20,
    },
});
