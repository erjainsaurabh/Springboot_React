#!/bin/bash

echo "Adding Prometheus datasource to Grafana..."

# Add Prometheus datasource
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
echo "Adding Loki datasource to Grafana..."

# Add Loki datasource
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
echo "You can now view the dashboards with data."
