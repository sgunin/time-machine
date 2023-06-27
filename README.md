# Time Machine

Система синхронизации времени высокой точности

## Структура проекта

* docs - документация
1. Модуль [ZED-F9T](https://www.u-blox.com/en/product/zed-f9t-module)
2. Модуль [GPS-18774](https://www.sparkfun.com/products/18774)
3. Антенна [GPS-17751](https://www.sparkfun.com/products/17751)
* hw - аппаратное обеспечение
* src - исходный код программного обеспечения
* tools - вспомогательное программное обеспечение

## Описание

Система синхронизации времени высокой точности основана на GNSS модуле компании UBlox [ZED-F9T](https://www.u-blox.com/en/product/zed-f9t-module).
Для обеспечения стабильного формирования синхросигнала в случае отсутствия данных от GNSS модуля, в качестве опорного генератора используется рубидиевый стандарт частоты.


## Ссылки

### [Time Appliances Project (TAP)](https://www.opencompute.org/projects/time-appliances-project-tap)
