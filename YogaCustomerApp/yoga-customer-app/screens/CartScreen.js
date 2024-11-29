import React, { useState, useEffect } from 'react';
import { View, Text, FlatList, StyleSheet, Button, Alert } from 'react-native';
import AsyncStorage from '@react-native-async-storage/async-storage';

export default function CartScreen() {
    const [cart, setCart] = useState([]);

    useEffect(() => {
        loadCart();
    }, []);

    const loadCart = async () => {
        try {
            const storedCart = JSON.parse(await AsyncStorage.getItem('cart')) || [];
            setCart(storedCart);
        } catch (error) {
            Alert.alert('Error', 'Could not load cart.');
        }
    };

    const clearCart = async () => {
        try {
            await AsyncStorage.removeItem('cart');
            setCart([]);
            Alert.alert('Success', 'Cart cleared!');
        } catch (error) {
            Alert.alert('Error', 'Could not clear cart.');
        }
    };

    return (
        <View style={styles.container}>
            <Text style={styles.title}>Your Booked Classes</Text>
            <FlatList
                data={cart}
                keyExtractor={(item, index) => index.toString()}
                renderItem={({ item }) => (
                    <View style={styles.classCard}>
                        <Text style={styles.classTitle}>{item.type}</Text>
                        <Text>{item.dayOfWeek} at {item.time}</Text>
                        <Text>Price: £{item.price}</Text>
                    </View>
                )}
            />
            <Button title="Clear Cart" onPress={clearCart} />
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
    classCard: {
        padding: 15,
        backgroundColor: '#fff',
        borderRadius: 8,
        marginBottom: 15,
        shadowColor: '#000',
        shadowOpacity: 0.1,
        shadowRadius: 5,
        elevation: 2,
    },
    classTitle: {
        fontSize: 18,
        fontWeight: 'bold',
    },
});
