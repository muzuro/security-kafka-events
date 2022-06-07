package com.mzr.sm.security.service

data class ServiceAccount(val name: String, val token: String, val permissions: List<String>)