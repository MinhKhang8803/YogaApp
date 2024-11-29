import React, { useState, useEffect } from 'react';
import { View, Text, Button, FlatList, TouchableOpacity, StyleSheet } from 'react-native';
import axios from 'axios';

export default function HomeScreen({ navigation }) {
    const [classes, setClasses] = useState([]);

    useEffect(() => {
        fetchClasses();
    }, []);

    const fetchClasses = async () => {
        try {
            const response = await axios.get('https://yogabackend-z0rp.onrender.com/classes');
            setClasses(response.data);
        } catch (error) {
            console.error('Error fetching classes:', error);
        }
    };

    return (
        <View style={styles.container}>
            <Text style={styles.title}>Available Yoga Classes</Text>
            <FlatList
                data={classes}
                keyExtractor={(item) => item.id}
                renderItem={({ item }) => (
                    <TouchableOpacity
                        style={styles.classCard}
                        onPress={() => navigation.navigate('ClassDetails', { classData: item })}
                    >
                        <Text style={styles.classTitle}>{item.type}</Text>
                        <Text>{item.dayOfWeek} at {item.time}</Text>
                        <Text>Price: £{item.price}</Text>
                    </TouchableOpacity>
                )}
            />
            <Button title="View Schedule" onPress={() => navigation.navigate('Schedule')} />
            <Button title="View Cart" onPress={() => navigation.navigate('Cart')} />
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
