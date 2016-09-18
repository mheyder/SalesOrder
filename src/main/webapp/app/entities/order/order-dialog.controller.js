(function() {
    'use strict';

    angular
        .module('salesOrderApp')
        .controller('OrderDialogController', OrderDialogController);

    OrderDialogController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModalInstance', '$q', 'entity', 'Order', 'User', 'Coupon', 'Shipment', 'ShippingAddress', 'OrderItem'];

    function OrderDialogController ($timeout, $scope, $stateParams, $uibModalInstance, $q, entity, Order, User, Coupon, Shipment, ShippingAddress, OrderItem) {
        var vm = this;

        vm.order = entity;
        vm.clear = clear;
        vm.datePickerOpenStatus = {};
        vm.openCalendar = openCalendar;
        vm.save = save;
        vm.users = User.query();
        vm.coupons = Coupon.query();
        vm.shipments = Shipment.query({filter: 'order-is-null'});
        $q.all([vm.order.$promise, vm.shipments.$promise]).then(function() {
            if (!vm.order.shipment || !vm.order.shipment.id) {
                return $q.reject();
            }
            return Shipment.get({id : vm.order.shipment.id}).$promise;
        }).then(function(shipment) {
            vm.shipments.push(shipment);
        });
        vm.shippingaddresses = ShippingAddress.query();
        vm.orderitems = OrderItem.query();

        $timeout(function (){
            angular.element('.form-group:eq(1)>input').focus();
        });

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function save () {
            vm.isSaving = true;
            if (vm.order.id !== null) {
                Order.update(vm.order, onSaveSuccess, onSaveError);
            } else {
                Order.save(vm.order, onSaveSuccess, onSaveError);
            }
        }

        function onSaveSuccess (result) {
            $scope.$emit('salesOrderApp:orderUpdate', result);
            $uibModalInstance.close(result);
            vm.isSaving = false;
        }

        function onSaveError () {
            vm.isSaving = false;
        }

        vm.datePickerOpenStatus.date = false;

        function openCalendar (date) {
            vm.datePickerOpenStatus[date] = true;
        }
    }
})();
