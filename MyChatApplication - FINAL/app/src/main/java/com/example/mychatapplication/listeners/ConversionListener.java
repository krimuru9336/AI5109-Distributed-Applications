package com.example.mychatapplication.listeners;

import com.example.mychatapplication.models.User;

public interface ConversionListener {
    void onEdit(int position);
    void onDelete(int position);
}
