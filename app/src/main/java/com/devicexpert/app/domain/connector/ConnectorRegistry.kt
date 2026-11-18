package com.devicexpert.app.domain.connector
import com.devicexpert.app.domain.model.device.DeviceCategory
interface ConnectorRegistry { fun getConnector(category: DeviceCategory): DeviceConnector }