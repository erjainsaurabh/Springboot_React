#!/bin/bash

# Wait for Grafana to be ready
echo "Waiting for Grafana to be ready..."
sleep 10

# Add Prometheus datasource
echo "Adding Prometheus datasource..."
curl -X POST \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Prometheus",
    "type": "prometheus",
    "access": "proxy",
    "url": "http://prometheus:9090",
    "uid": "prometheus",
    "isDefault": true,
    "editable": true
  }' \
  http://admin:admin@localhost:3000/api/datasources

echo ""

# Add Loki datasource
echo "Adding Loki datasource..."
curl -X POST \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Loki",
    "type": "loki",
    "access": "proxy",
    "url": "http://loki:3100",
    "uid": "Loki",
    "editable": true
  }' \
  http://admin:admin@localhost:3000/api/datasources

echo ""
echo "Datasources added successfully!"
