package com.mzr.sm.security.service

data class ServiceAccount(val token: String, val permissions: List<String>)