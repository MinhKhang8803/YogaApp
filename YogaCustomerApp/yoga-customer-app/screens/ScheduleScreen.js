import React, { useState, useEffect } from 'react';
import { View, Text, StyleSheet, ScrollView, Alert } from 'react-native';
import { Calendar } from 'react-native-calendars';
import axios from 'axios';
import AsyncStorage from '@react-native-async-storage/async-storage';
import moment from 'moment';

export default function ScheduleScreen() {
    const [markedDates, setMarkedDates] = useState({});
    const [selectedDate, setSelectedDate] = useState(moment().format('YYYY-MM-DD'));
    const [upcomingClasses, setUpcomingClasses] = useState([]);
    const [pastClasses, setPastClasses] = useState([]);

    useEffect(() => {
        fetchSchedule();
    }, []);

    const fetchSchedule = async () => {
        try {
            const cart = JSON.parse(await AsyncStorage.getItem('cart')) || [];
            const classIds = cart.map((cls) => cls.id);
    
            const response = await axios.post('https://yogabackend-z0rp.onrender.com/schedule', { classIds });
            const scheduleData = response.data;
    
            const now = moment();
            const marked = {};
            const upcoming = [];
            const past = [];
    
            scheduleData.forEach((item) => {
                // Chuyển đổi ngày từ DD/MM/YYYY sang YYYY-MM-DD
                const formattedDate = moment(item.date, 'DD/MM/YYYY').format('YYYY-MM-DD');
                const classDate = moment(formattedDate);
    
                if (classDate.isSameOrAfter(now, 'day')) {
                    upcoming.push({ ...item, date: formattedDate });
                    marked[formattedDate] = { marked: true, dotColor: 'green' };
                } else {
                    past.push({ ...item, date: formattedDate });
                    marked[formattedDate] = { marked: true, dotColor: 'gray' };
                }
            });
    
            setUpcomingClasses(upcoming);
            setPastClasses(past);
    
            setMarkedDates({
                ...marked,
                [selectedDate]: { ...marked[selectedDate], selected: true, selectedColor: '#f09' },
            });
        } catch (error) {
            Alert.alert('Error', 'Could not fetch schedule.');
            console.error('Error fetching schedule:', error);
        }
    };
    

    return (
        <ScrollView style={styles.container}>
            <Text style={styles.title}>Your Schedule</Text>

            <Calendar
                onDayPress={(day) => setSelectedDate(day.dateString)}
                markedDates={{
                    ...markedDates,
                    [selectedDate]: { ...markedDates[selectedDate], selected: true, selectedColor: '#f09' },
                }}
            />

            <Text style={styles.sectionTitle}>Upcoming Classes</Text>
            {upcomingClasses.length > 0 ? (
                upcomingClasses.map((item, index) => (
                    <View key={index} style={styles.classCard}>
                        <Text style={styles.classType}>{item.type}</Text>
                        <Text>Date: {item.date}</Text>
                        <Text>Teacher: {item.teacher}</Text>
                    </View>
                ))
            ) : (
                <Text style={styles.noClassesText}>No upcoming classes.</Text>
            )}

            <Text style={styles.sectionTitle}>Past Classes</Text>
            {pastClasses.length > 0 ? (
                pastClasses.map((item, index) => (
                    <View key={index} style={styles.classCard}>
                        <Text style={styles.classType}>{item.type}</Text>
                        <Text>Date: {item.date}</Text>
                        <Text>Teacher: {item.teacher}</Text>
                    </View>
                ))
            ) : (
                <Text style={styles.noClassesText}>No past classes.</Text>
            )}
        </ScrollView>
    );
}

const styles = StyleSheet.create({
    container: {
        flex: 1,
        backgroundColor: '#f5f5f5',
        padding: 20,
    },
    title: {
        fontSize: 24,
        fontWeight: 'bold',
        textAlign: 'center',
        marginVertical: 10,
    },
    sectionTitle: {
        fontSize: 20,
        fontWeight: 'bold',
        marginTop: 20,
        marginBottom: 10,
    },
    classCard: {
        padding: 15,
        backgroundColor: '#fff',
        borderRadius: 8,
        marginBottom: 10,
        shadowColor: '#000',
        shadowOpacity: 0.1,
        shadowRadius: 5,
        elevation: 2,
    },
    classType: {
        fontSize: 16,
        fontWeight: 'bold',
    },
    noClassesText: {
        fontSize: 14,
        color: '#999',
        textAlign: 'center',
    },
});
